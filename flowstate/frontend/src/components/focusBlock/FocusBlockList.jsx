import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import CapacityBar from '../common/CapacityBar';
import FocusBlockForm, { DAYS, titleCase } from './FocusBlockForm';
import { deleteBlock, fetchMyBlocks, protectBlock, unprotectBlock } from '../../store/slices/focusBlockSlice';

const FocusBlockList = () => {
  const dispatch = useDispatch();
  const { items, error } = useSelector((state) => state.focusBlocks);
  const [showForm, setShowForm] = useState(false);

  useEffect(() => {
    dispatch(fetchMyBlocks());
  }, [dispatch]);

  const handleDelete = (block) => {
    if (window.confirm('Delete this focus block?')) dispatch(deleteBlock(block.id));
  };

  return (
    <div className="page-container">
      <h1>Weekly Focus Blocks</h1>
      <button type="button" className="primary-btn" onClick={() => setShowForm(true)}>
        + Add Block
      </button>

      {error && <div className="alert error">{error}</div>}
      <CapacityBar blocks={items} />

      <div className="week-list">
        {DAYS.map((day) => {
          const blocks = items
            .filter((b) => b.dayOfWeek === day)
            .sort((a, b) => String(a.blockStartTime).localeCompare(String(b.blockStartTime)));
          return (
            <div key={day} className="day-section">
              <h3>{titleCase(day)}</h3>
              {blocks.length === 0 ? (
                <p className="muted">No blocks</p>
              ) : (
                blocks.map((b) => (
                  <div key={b.id} className="block-row">
                    <span className="strong">
                      {b.blockStartTime} – {b.blockEndTime}
                    </span>
                    {b.preferredSessionType && (
                      <span className="muted">{String(b.preferredSessionType).replace(/_/g, ' ')}</span>
                    )}
                    {b.isProtected && <span className="badge badge-completed">Protected</span>}
                    <span className="actions">
                      {b.isProtected ? (
                        <button type="button" className="secondary-btn" onClick={() => dispatch(unprotectBlock(b.id))}>
                          Unprotect
                        </button>
                      ) : (
                        <button type="button" className="secondary-btn" onClick={() => dispatch(protectBlock(b.id))}>
                          Protect
                        </button>
                      )}
                      <button type="button" className="secondary-btn" onClick={() => handleDelete(b)}>
                        Delete
                      </button>
                    </span>
                  </div>
                ))
              )}
            </div>
          );
        })}
      </div>

      {showForm && <FocusBlockForm onClose={() => setShowForm(false)} />}
    </div>
  );
};

export default FocusBlockList;
