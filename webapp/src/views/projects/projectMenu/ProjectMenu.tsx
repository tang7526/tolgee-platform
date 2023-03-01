import { Devices, PersonOutline, VpnKey } from '@mui/icons-material';
import { useTranslate } from '@tolgee/react';
import LanguageIcon from '@mui/icons-material/Language';
import {
  ExportIcon,
  ImportIcon,
  ProjectsIcon,
  SettingsIcon,
  TranslationIcon,
} from 'tg.component/CustomIcons';
import { LINKS, PARAMS } from 'tg.constants/links';
import { useConfig } from 'tg.globalContext/helpers';

import { SideMenu } from './SideMenu';
import { SideMenuItem } from './SideMenuItem';
import { SideLogo } from './SideLogo';
import { useTopBarHidden } from 'tg.component/layout/TopBar/TopBarContext';
import DashboardIcon from '@mui/icons-material/Dashboard';
import { useProjectPermissions } from 'tg.hooks/useProjectPermissions';

export const ProjectMenu = ({ id }) => {
  const { satisfiesPermission } = useProjectPermissions();
  const config = useConfig();

  const canViewKeys = satisfiesPermission('keys.view');
  const canViewTranslations = satisfiesPermission('translations.view');
  const canEditProject = satisfiesPermission('project.edit');
  const canEditLanguages = satisfiesPermission('languages.edit');
  const canViewUsers = satisfiesPermission('users.view');
  const canImport =
    (satisfiesPermission('translations.edit') ||
      satisfiesPermission('keys.edit')) &&
    satisfiesPermission('keys.view');

  const { t } = useTranslate();

  const topBarHidden = useTopBarHidden();

  return (
    <SideMenu>
      <SideLogo hidden={!topBarHidden} />
      <SideMenuItem
        linkTo={LINKS.PROJECTS.build({ [PARAMS.PROJECT_ID]: id })}
        icon={<ProjectsIcon />}
        text={t('project_menu_projects')}
      />
      <SideMenuItem
        linkTo={LINKS.PROJECT_DASHBOARD.build({ [PARAMS.PROJECT_ID]: id })}
        icon={<DashboardIcon />}
        text={t('project_menu_dashboard', 'Project Dashboard')}
      />
      {canViewKeys && (
        <SideMenuItem
          linkTo={LINKS.PROJECT_TRANSLATIONS.build({
            [PARAMS.PROJECT_ID]: id,
          })}
          icon={<TranslationIcon />}
          text={t('project_menu_translations')}
          matchAsPrefix
        />
      )}
      <>
        {canEditProject && (
          <SideMenuItem
            linkTo={LINKS.PROJECT_EDIT.build({
              [PARAMS.PROJECT_ID]: id,
            })}
            matchAsPrefix
            icon={<SettingsIcon />}
            text={t('project_menu_project_settings')}
          />
        )}
        {canEditLanguages && (
          <SideMenuItem
            linkTo={LINKS.PROJECT_LANGUAGES.build({
              [PARAMS.PROJECT_ID]: id,
            })}
            matchAsPrefix
            icon={<LanguageIcon />}
            text={t('project_menu_languages')}
          />
        )}
        {config.authentication && canViewUsers && (
          <>
            <SideMenuItem
              linkTo={LINKS.PROJECT_PERMISSIONS.build({
                [PARAMS.PROJECT_ID]: id,
              })}
              icon={<PersonOutline />}
              text={t('project_menu_members')}
            />
          </>
        )}
        {canImport && (
          <SideMenuItem
            linkTo={LINKS.PROJECT_IMPORT.build({
              [PARAMS.PROJECT_ID]: id,
            })}
            icon={<ImportIcon />}
            text={t('project_menu_import')}
          />
        )}
      </>

      {canViewTranslations && (
        <SideMenuItem
          linkTo={LINKS.PROJECT_EXPORT.build({
            [PARAMS.PROJECT_ID]: id,
          })}
          icon={<ExportIcon />}
          text={t('project_menu_export')}
        />
      )}
      <SideMenuItem
        linkTo={LINKS.PROJECT_INTEGRATE.build({
          [PARAMS.PROJECT_ID]: id,
        })}
        icon={<Devices />}
        text={t('project_menu_integrate')}
      />
      {!config.authentication && (
        <SideMenuItem
          linkTo={LINKS.USER_API_KEYS.build()}
          icon={<VpnKey />}
          text={t('project_menu_api_keys')}
        />
      )}
    </SideMenu>
  );
};
