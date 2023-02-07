import {
  HierarchyItem,
  PermissionState,
} from 'tg.component/PermissionsSettings/types';
import { Hierarchy } from './Hierarchy';
import { useTranslate } from '@tolgee/react';

type Props = {
  dependencies: HierarchyItem;
  state: PermissionState;
  onChange: (value: PermissionState) => void;
};

export const PermissionsAdvanced: React.FC<Props> = ({
  dependencies,
  state,
  onChange,
}) => {
  const { t } = useTranslate();

  return (
    <Hierarchy
      dependencies={dependencies}
      state={state}
      onChange={onChange}
      structure={{
        value: 'admin',
        children: [
          {
            label: t('permissions_item_translations'),
            children: [
              {
                value: 'translations.view',
              },
              {
                value: 'translations.edit',
              },
              {
                value: 'translations.state-edit',
              },
              {
                label: t('permissions_item_translations_comments'),
                children: [
                  {
                    value: 'translation-comments.add',
                  },
                  {
                    value: 'translation-comments.edit',
                  },
                  {
                    value: 'translation-comments.set-state',
                  },
                ],
              },
            ],
          },
          {
            label: t('permissions_item_screenshots'),
            children: [
              {
                value: 'screenshots.view',
              },
              {
                value: 'screenshots.upload',
              },
              {
                value: 'screenshots.delete',
              },
            ],
          },
          {
            value: 'keys.edit',
          },
          {
            value: 'users.view',
          },
          {
            value: 'project.edit',
          },
          {
            value: 'activity.view',
          },
          {
            value: 'languages.edit',
          },
          {
            value: 'import',
          },
        ],
      }}
    />
  );
};