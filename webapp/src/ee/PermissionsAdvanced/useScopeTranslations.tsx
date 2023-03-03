import { useTranslate } from '@tolgee/react';
import { PermissionModelScope } from 'tg.component/PermissionsSettings/types';

export const useScopeTranslations = () => {
  const { t } = useTranslate();

  const labels: Record<PermissionModelScope, string> = {
    admin: t('permissions_item_admin'),
    'translations.view': t('permissions_item_translations_view'),
    'translations.edit': t('permissions_item_translations_edit'),
    'translation-comments.add': t('permissions_item_translations_comments_add'),
    'translation-comments.edit': t(
      'permissions_item_translations_comments_edit'
    ),
    'translation-comments.set-state': t(
      'permissions_item_translations_comments_set_state'
    ),
    'translations.state-edit': t('permissions_item_translations_state'),
    'screenshots.view': t('permissions_item_screenshots_view'),
    'screenshots.upload': t('permissions_item_screenshots_upload'),
    'screenshots.delete': t('permissions_item_screenshots_delete'),
    'keys.create': t('permissions_item_keys_create'),
    'keys.edit': t('permissions_item_keys_edit'),
    'keys.view': t('permissions_item_keys_view'),
    'keys.delete': t('permissions_item_keys_delete'),
    'project.edit': t('permissions_item_project_edit'),
    'members.view': t('permissions_item_members_view'),
    'members.edit': t('permissions_item_members_edit'),
    'languages.edit': t('permissions_item_languages_edit'),
    'activity.view': t('permissions_item_activity_view'),
  };

  return {
    getScopeTranslation: (scope: PermissionModelScope) => labels[scope],
  };
};
