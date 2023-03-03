import { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { container } from 'tsyringe';

import { AppState } from 'tg.store/index';
import { useApiMutation, useApiQuery } from 'tg.service/http/useQueryApi';
import { GlobalActions } from 'tg.store/global/GlobalActions';
import { components } from 'tg.service/apiSchema.generated';
import { InvitationCodeService } from 'tg.service/InvitationCodeService';
import { useTolgee } from '@tolgee/react';
import { useOnUpdate } from 'tg.hooks/useOnUpdate';

type PrivateOrganizationModel =
  components['schemas']['PrivateOrganizationModel'];

export const useInitialDataService = () => {
  const actions = container.resolve(GlobalActions);
  const invitationCodeService = container.resolve(InvitationCodeService);
  const tolgee = useTolgee();

  const [organization, setOrganization] = useState<
    PrivateOrganizationModel | undefined
  >(undefined);
  const security = useSelector((state: AppState) => state.global.security);
  const initialData = useApiQuery({
    url: '/v2/public/initial-data',
    method: 'get',
    options: {
      refetchOnMount: false,
      cacheTime: Infinity,
      keepPreviousData: true,
      staleTime: Infinity,
    },
  });

  useEffect(() => {
    const data = initialData.data;
    if (data) {
      // set organization data only if missing
      setOrganization((org) => (org ? org : data.preferredOrganization));
      if (data.languageTag) {
        // switch ui language, once user is signed in
        tolgee.changeLanguage(data.languageTag);
      }
      const invitationCode = invitationCodeService.getCode();
      actions.updateSecurity.dispatch({
        allowPrivate:
          !data?.serverConfiguration?.authentication || Boolean(data.userInfo),
        allowRegistration:
          data.serverConfiguration.allowRegistrations ||
          Boolean(invitationCode), // if user has invitation code, registration is allowed
      });
    }
  }, [Boolean(initialData.data)]);

  const preferredOrganizationLoadable = useApiMutation({
    url: '/v2/preferred-organization',
    method: 'get',
  });

  const setPreferredOrganization = useApiMutation({
    url: '/v2/user-preferences/set-preferred-organization/{organizationId}',
    method: 'put',
  });

  const preferredOrganization =
    organization ?? initialData.data?.preferredOrganization;

  const updatePreferredOrganization = async (organizationId: number) => {
    if (organizationId !== preferredOrganization?.id) {
      // set preffered organization
      await setPreferredOrganization.mutateAsync({
        path: { organizationId },
      });

      // load new preferred organization
      const data = await preferredOrganizationLoadable.mutateAsync({});
      setOrganization(data);
    }
  };

  const refetchInitialData = () => {
    setOrganization(undefined);
    return initialData.refetch();
  };

  useOnUpdate(() => {
    refetchInitialData();
  }, [security.jwtToken]);

  const isFetching =
    initialData.isFetching ||
    setPreferredOrganization.isLoading ||
    preferredOrganizationLoadable.isLoading;

  if (initialData.error) {
    throw new Error(initialData.error.message || initialData.error);
  }

  return {
    data: {
      ...initialData.data!,
      preferredOrganization,
    },
    isFetching,
    isLoading: initialData.isLoading,

    refetchInitialData,
    updatePreferredOrganization,
  };
};
