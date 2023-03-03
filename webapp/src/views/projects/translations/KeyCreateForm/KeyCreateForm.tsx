import { useEffect } from 'react';
import { T } from '@tolgee/react';
import { Formik } from 'formik';
import * as Yup from 'yup';
import { container } from 'tsyringe';

import { components } from 'tg.service/apiSchema.generated';
import { useProject } from 'tg.hooks/useProject';
import { useApiMutation } from 'tg.service/http/useQueryApi';
import { useUrlSearch } from 'tg.hooks/useUrlSearch';
import { parseErrorResponse } from 'tg.fixtures/errorFIxtures';
import { LINKS } from 'tg.constants/links';
import { MessageService } from 'tg.service/MessageService';
import { useProjectPermissions } from 'tg.hooks/useProjectPermissions';
import { RedirectionActions } from 'tg.store/global/RedirectionActions';
import { FormBody } from './FormBody';
import { useGlobalActions } from 'tg.globalContext/GlobalContext';

type KeyWithDataModel = components['schemas']['KeyWithDataModel'];
type LanguageModel = components['schemas']['LanguageModel'];

const messaging = container.resolve(MessageService);
const redirectionActions = container.resolve(RedirectionActions);

export type ValuesCreateType = {
  name: string;
  translations: Record<string, string>;
  tags: string[];
};

type Props = {
  languages: LanguageModel[];
  onSuccess?: (data: KeyWithDataModel) => void;
  onCancel?: () => void;
  autofocus?: boolean;
};

export const KeyCreateForm: React.FC<Props> = ({
  languages,
  onSuccess,
  onCancel,
  autofocus,
}) => {
  const project = useProject();
  const permissions = useProjectPermissions();
  const { refetchUsage } = useGlobalActions();

  const keyName = useUrlSearch().key as string;
  const namespace = useUrlSearch().ns as string;

  const createKey = useApiMutation({
    url: '/v2/projects/{projectId}/keys/create',
    method: 'post',
  });

  const handleSubmit = (values: ValuesCreateType) => {
    return createKey.mutateAsync(
      {
        path: { projectId: project.id },
        content: { 'application/json': values },
      },
      {
        onSuccess(data) {
          messaging.success(<T>translations_key_created</T>);
          onSuccess?.(data);
          refetchUsage();
        },
        onError(e) {
          parseErrorResponse(e).forEach((message) =>
            messaging.error(<T>{message}</T>)
          );
        },
      }
    );
  };

  const translationValues = {};
  languages.forEach(({ tag }) => {
    translationValues[tag] = '';
  });

  const canCreateKeys = permissions.satisfiesPermission('keys.create');
  useEffect(() => {
    if (!canCreateKeys) {
      redirectionActions.redirect.dispatch(LINKS.AFTER_LOGIN.build());
      messaging.error(<T>translation_single_no_permission_create</T>);
    }
  }, [canCreateKeys]);

  return canCreateKeys ? (
    <Formik
      initialValues={{
        name: keyName,
        translations: translationValues,
        tags: [],
        namespace: namespace,
      }}
      onSubmit={handleSubmit}
      validationSchema={Yup.object().shape({
        name: Yup.string().required(),
      })}
    >
      <FormBody
        languages={languages}
        onCancel={onCancel}
        autofocus={autofocus}
      />
    </Formik>
  ) : null;
};
