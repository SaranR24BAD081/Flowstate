import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { clearBlockError, createBlock } from '../../store/slices/focusBlockSlice';

export const DAYS = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];
export const titleCase = (s) => s.charAt(0) + s.slice(1).toLowerCase();

const FocusBlockForm = ({ onClose }) => {
  const dispatch = useDispatch();
  const { error } = useSelector((state) => state.focusBlocks);

  const [dayOfWeek, setDayOfWeek] = useState('MONDAY');
  const [blockStartTime, setStart] = useState('09:00');
  const [blockEndTime, setEnd] = useState('11:00');
  const [preferredSessionType, setType] = useState('DEEP_WORK');
  const [localError, setLocalError] = useState('');

  useEffect(() => () => dispatch(clearBlockError()), [dispatch]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLocalError('');
    if (blockEndTime <= blockStartTime) {
      setLocalError('End time must be after start time.');
      return;
    }
    const result = await dispatch(createBlock({ dayOfWeek, blockStartTime, blockEndTime, preferredSessionType }));
    if (createBlock.fulfilled.match(result)) onClose();
  };

  const shownError = localError || error;

  return (
    <div className="modal-overlay">
      <div className="modal-card">
        <div className="modal-header">
          <h2>Register Focus Block</h2>
          <button type="button" className="close-btn" aria-label="Close" onClick={onClose}>
            ×
          </button>
        </div>
        {shownError && <div className="error-banner">{shownError}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="dayOfWeek">Day of Week *</label>
            <select id="dayOfWeek" value={dayOfWeek} onChange={(e) => setDayOfWeek(e.target.value)}>
              {DAYS.map((d) => (
                <option key={d} value={d}>
                  {titleCase(d)}
                </option>
              ))}
            </select>
          </div>
          <div className="form-row">
            <div className="form-group">
              <label htmlFor="blockStartTime">Start Time *</label>
              <input id="blockStartTime" type="time" value={blockStartTime} onChange={(e) => setStart(e.target.value)} required />
            </div>
            <div className="form-group">
              <label htmlFor="blockEndTime">End Time *</label>
              <input id="blockEndTime" type="time" value={blockEndTime} onChange={(e) => setEnd(e.target.value)} required />
            </div>
          </div>
          <div className="form-group">
            <label htmlFor="preferredSessionType">Preferred Session Type</label>
            <select id="preferredSessionType" value={preferredSessionType} onChange={(e) => setType(e.target.value)}>
              <option value="DEEP_WORK">Deep Work</option>
              <option value="CREATIVE">Creative</option>
              <option value="REVIEW">Review</option>
            </select>
          </div>
          <button type="submit" className="primary-btn full-width">
            Register Block
          </button>
        </form>
      </div>
    </div>
  );
};

export default FocusBlockForm;
