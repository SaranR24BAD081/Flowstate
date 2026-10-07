import React from 'react';

const SearchFilterBar = ({
  placeholder = 'Search...',
  searchValue = '',
  onSearchChange,
  statusValue = '',
  onStatusChange,
  statusOptions = [],
  allLabel = 'All Statuses',
}) => (
  <div className="search-filter-bar">
    <input
      type="text"
      placeholder={placeholder}
      value={searchValue}
      onChange={(e) => onSearchChange && onSearchChange(e.target.value)}
    />
    <select
      aria-label="Filter by status"
      value={statusValue}
      onChange={(e) => onStatusChange && onStatusChange(e.target.value)}
    >
      <option value="">{allLabel}</option>
      {statusOptions.map((s) => (
        <option key={s} value={s}>
          {s.replace(/_/g, ' ')}
        </option>
      ))}
    </select>
  </div>
);

export default SearchFilterBar;
