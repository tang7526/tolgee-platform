import { ProjectDTO } from '../../../../../webapp/src/service/response.types';
import { internalFetch } from '../common';
import { cleanTestData, generateTestDataObject } from './generator';
import { components } from '../../../../../webapp/src/service/apiSchema.generated';

type PermissionModelScopes = components['schemas']['PermissionModel']['scopes'];

export const organizationTestData = generateTestDataObject('organizations');

export const organizationNewTestData =
  generateTestDataObject('organization-new');

export const languagesTestData = generateTestDataObject('languages');

export const commentsTestData = generateTestDataObject('translation-comments');

export const translationSingleTestData =
  generateTestDataObject('translation-single');

export const importTestData = {
  clean: () => cleanTestData('import'),
  generateBasic: () => internalFetch('e2e-data/import/generate'),
  generateApplicable: () =>
    internalFetch('e2e-data/import/generate-applicable'),
  generateAllSelected: () =>
    internalFetch('e2e-data/import/generate-all-selected'),
  generateLotOfData: () =>
    internalFetch('e2e-data/import/generate-lot-of-data'),
  generateBase: () => internalFetch('e2e-data/import/generate-base'),
  generateWithManyLanguages: () =>
    internalFetch('e2e-data/import/generate-many-languages'),
  generateWithLongText: () =>
    internalFetch('e2e-data/import/generate-with-long-text'),
};

export const importNamespacesTestData =
  generateTestDataObject('import-namespaces');

export const projectListdData = generateTestDataObject(
  'projects-list-dashboard'
);

export const projectTestData = generateTestDataObject('projects');

export const apiKeysTestData = generateTestDataObject('api-keys');

export const patsTestData = generateTestDataObject('pat');

export const languagePermissionsData = generateTestDataObject(
  'language-permissions'
);

export const generateExampleKeys = (
  projectId: number,
  numberOfExamples: number
) =>
  internalFetch(
    `e2e-data/translations/generate/${projectId}/${numberOfExamples}`
  );

export const translationsTestData = {
  generateExampleKeys: (projectId: number, numberOfExamples: number) =>
    internalFetch(
      `e2e-data/translations/generate/${projectId}/${numberOfExamples}`
    ),

  cleanupForFilters: () =>
    internalFetch('e2e-data/translations/cleanup-for-filters'),

  generateForFilters: () =>
    internalFetch('e2e-data/translations/generate-for-filters').then(
      (r) => r.body as ProjectDTO
    ),
};

export const translationsNsAndTagsTestData =
  generateTestDataObject('ns-and-tags');

export const projectLeavingTestData = generateTestDataObject('project-leaving');

export const projectTransferringTestData = generateTestDataObject(
  'project-transferring'
);

export const avatarTestData = generateTestDataObject('avatars');

export const administrationTestData = generateTestDataObject('administration');

export const translationWebsocketsTestData = generateTestDataObject(
  'websocket-translations'
);

export const userDeletionTestData = generateTestDataObject('user-deletion');

export const formerUserTestData = generateTestDataObject('former-user');

export const namespaces = generateTestDataObject('namespaces');

export const sensitiveOperationProtectionTestData = {
  ...generateTestDataObject('sensitive-operation-protection'),
  getOtp: () =>
    internalFetch(`e2e-data/sensitive-operation-protection/get-totp`),
};

export type PermissionsOptions = {
  scopes: PermissionModelScopes;
  translateLanguages?: string[];
  viewLanguages?: string[];
  stateChangeLanguages?: string[];
};

export const generatePermissionsData = {
  generate: (options: Partial<PermissionsOptions>) => {
    const params = new URLSearchParams();
    Object.entries(options).forEach(([key, values]) => {
      values.forEach((value) => {
        if (key === 'scopes') {
          params.append(key, value.replace(/\.|-/g, '_').toUpperCase());
        } else {
          params.append(key, value);
        }
      });
    });
    return internalFetch(
      `e2e-data/permissions/generate-with-user?${params.toString()}`
    );
  },
  clean: () => {
    return internalFetch('e2e-data/permissions/clean');
  },
};
