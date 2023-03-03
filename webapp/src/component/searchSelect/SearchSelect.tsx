import { useRef, useState } from 'react';
import { Box, Select, styled } from '@mui/material';

import { SearchSelectPopover } from './SearchSelectPopover';
import { SearchSelectContent } from './SearchSelectContent';
import { SearchSelectMulti } from './SearchSelectMulti';

export type SelectItem<T> = {
  value: T;
  name: string;
};

const StyledInputContent = styled('div')`
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-right: -5px;
  contain: size;
  height: 23px;
`;

type Props<T> = {
  onChange?: (value: T | T[]) => void;
  onSelect?: (value: T) => void;
  anchorEl?: HTMLElement;
  items: SelectItem<T>[];
  value: T | T[] | undefined;
  onAddNew?: (searchValue: string) => void;
  searchPlaceholder?: string;
  title?: string;
  addNewTooltip?: string;
  displaySearch?: boolean;
  popperMinWidth?: string | number;
  multiple?: boolean;
  renderValue?: (value: T | T[] | undefined) => React.ReactNode;
  SelectProps?: React.ComponentProps<typeof Select>;
};

export function SearchSelect<T extends React.Key>({
  onChange,
  onSelect,
  items,
  value,
  onAddNew,
  searchPlaceholder,
  title,
  addNewTooltip,
  displaySearch = true,
  popperMinWidth,
  multiple,
  renderValue,
  SelectProps,
}: Props<T>) {
  const anchorEl = useRef<HTMLAnchorElement>(null);
  const [isOpen, setIsOpen] = useState(false);

  const handleClose = () => {
    setIsOpen(false);
  };

  const handleOpen = () => {
    setIsOpen(true);
  };

  const handleSelect = (newValue: T) => {
    onSelect?.(newValue);
    if (multiple && Array.isArray(value)) {
      const newValues = value.includes(newValue)
        ? value.filter((i) => i !== newValue)
        : [...value, newValue];
      onChange?.(newValues);
    } else {
      onChange?.(newValue);
    }
  };

  const myRenderValue = () => (
    <StyledInputContent>
      {renderValue
        ? renderValue(value)
        : (valueItem ? valueItem.name : value) || ''}
    </StyledInputContent>
  );

  const handleOnAddNew = (searchValue: string) => {
    setIsOpen(false);
    onAddNew?.(searchValue);
  };

  const valueItem = items.find((i) => i.value === value);

  return (
    <Box display="flex" data-cy="search-select">
      <Select
        ref={anchorEl}
        onOpen={handleOpen}
        fullWidth
        MenuProps={{ variant: 'menu' }}
        open={false}
        value={myRenderValue()}
        displayEmpty
        multiple
        renderValue={myRenderValue}
        {...SelectProps}
      />

      <SearchSelectPopover
        open={isOpen}
        onClose={handleClose}
        anchorEl={anchorEl.current!}
      >
        {multiple ? (
          <SearchSelectMulti
            open={isOpen}
            onClose={handleClose}
            value={value as T[]}
            onSelect={handleSelect}
            anchorEl={anchorEl.current!}
            items={items}
            onAddNew={handleOnAddNew}
            displaySearch={displaySearch}
            searchPlaceholder={searchPlaceholder}
            title={title}
            addNewTooltip={addNewTooltip}
            minWidth={popperMinWidth}
          />
        ) : (
          <SearchSelectContent
            open={isOpen}
            onClose={handleClose}
            selected={value as string | undefined}
            onSelect={handleSelect}
            anchorEl={anchorEl.current!}
            items={items}
            onAddNew={handleOnAddNew}
            displaySearch={displaySearch}
            searchPlaceholder={searchPlaceholder}
            title={title}
            addNewTooltip={addNewTooltip}
            minWidth={popperMinWidth}
          />
        )}
      </SearchSelectPopover>
    </Box>
  );
}
