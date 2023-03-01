import { styled, Checkbox, FormControlLabel } from '@mui/material';
import {
  HierarchyItem,
  HierarchyType,
  PermissionAdvancedState,
  PermissionModelScope,
} from 'tg.component/PermissionsSettings/types';
import { LanguagePermissionsMenu } from 'tg.component/security/LanguagePermissionsMenu';
import { useProjectLanguages } from 'tg.hooks/useProjectLanguages';
import {
  checkChildren,
  getChildScopes,
  getMinimalLanguages,
  getScopeLanguagePermission,
  updateByDependencies,
  getBlockingScopes,
  ALL_LANGUAGES_SCOPES,
  isAllLanguages,
} from './hierarchyTools';
import { useScopeTranslations } from './useScopeTranslations';

const StyledContainer = styled('div')`
  display: grid;
  justify-content: start;
`;

const StyledChildren = styled('div')`
  display: grid;
  margin-left: 32px;
`;

const StyledRow = styled('div')`
  display: grid;
  grid-template-columns: minmax(150px, auto) auto;
  gap: 16px;
  align-items: center;
`;

type Props = {
  dependencies: HierarchyItem;
  structure: HierarchyType;
  state: PermissionAdvancedState;
  onChange: (value: PermissionAdvancedState) => void;
};

export const Hierarchy: React.FC<Props> = ({
  dependencies,
  structure,
  state,
  onChange,
}) => {
  const languages = useProjectLanguages();
  const allLangs = languages.map((l) => l.id);
  const { scopes } = state;
  const scopeIncluded = structure.value && scopes.includes(structure.value);
  const { childrenCheckedSome, childrenCheckedAll } = checkChildren(
    structure,
    scopes
  );

  // scopes the item is responsible for
  const myScopes = structure.value
    ? [structure.value]
    : getChildScopes(structure);

  const myLanguageProps = myScopes
    .map(getScopeLanguagePermission)
    .filter(Boolean);

  // check if all dependant scopes are in responsible nodes
  // meaning if we toggle this, nothing outside gets broken
  const blockingScopes = getBlockingScopes(myScopes, scopes, dependencies);

  const blockedLanguages = getMinimalLanguages(blockingScopes, state, allLangs);

  const disabled = Boolean(blockingScopes.length);

  const fullyChecked =
    scopeIncluded || (!structure.value && childrenCheckedAll);
  const halfChecked = !structure.value && !fullyChecked && childrenCheckedSome;

  const { getScopeTranslation } = useScopeTranslations();

  const label =
    structure.label ||
    (structure.value ? getScopeTranslation(structure.value) : undefined);

  const minimalLanguages = getMinimalLanguages(
    structure.value
      ? [structure.value]
      : myScopes.filter((sc) => scopes.includes(sc)),
    state,
    []
  );

  const displayLanguages =
    allLangs.length > 1 &&
    minimalLanguages &&
    structure.value &&
    !ALL_LANGUAGES_SCOPES.includes(structure.value!);

  const updateScopes = (scopes: PermissionModelScope[], value: boolean) => {
    let newScopes = [...state.scopes];
    scopes.forEach((scope) => {
      const exists = newScopes.includes(scope);
      if (exists && value === false) {
        newScopes = newScopes.filter((s) => s !== scope);
      } else if (!exists && value === true) {
        newScopes = [...newScopes, scope];
      }
    });
    return newScopes;
  };

  const updateLanguagePermissions = (value: number[]) => {
    const affectedScopes = myScopes.filter((sc) => scopes.includes(sc));
    const newState = {
      ...state,
    };
    myLanguageProps.forEach((langProp) => {
      newState[langProp!] = value;
    });
    onChange(
      updateByDependencies(affectedScopes, newState, dependencies, allLangs)
    );
  };

  const handleToggle = () => {
    if (fullyChecked) {
      onChange({
        ...state,
        scopes: updateScopes(myScopes, false),
      });
    } else {
      // get myScopes and also their required scopes
      onChange(updateByDependencies(myScopes, state, dependencies, allLangs));
    }
  };

  return (
    <StyledContainer>
      <StyledRow>
        <FormControlLabel
          control={
            <Checkbox
              checked={fullyChecked}
              indeterminate={halfChecked}
              onClick={handleToggle}
              disabled={disabled}
            />
          }
          label={label}
        />

        {minimalLanguages && displayLanguages && (
          <LanguagePermissionsMenu
            buttonProps={{ size: 'small' }}
            disabled={
              (!halfChecked && !fullyChecked) ||
              Boolean(
                blockingScopes.find((scope) =>
                  ALL_LANGUAGES_SCOPES.includes(scope)
                )
              ) ||
              blockedLanguages
            }
            selected={
              !blockedLanguages
                ? minimalLanguages
                : isAllLanguages(blockedLanguages, allLangs) &&
                  isAllLanguages(minimalLanguages, allLangs)
                ? []
                : minimalLanguages
            }
            onSelect={(value) => updateLanguagePermissions(value)}
          />
        )}
      </StyledRow>
      <StyledChildren>
        {structure.children?.map((child, i) => {
          return (
            <Hierarchy
              key={`${child.value}.${i}`}
              dependencies={dependencies}
              structure={child}
              state={state}
              onChange={onChange}
            />
          );
        })}
      </StyledChildren>
    </StyledContainer>
  );
};
