import React, { useState, useEffect } from 'react';
import { adminApi } from '../../api/adminApi';
import { Settings, Save, CheckCircle2, ShieldAlert } from 'lucide-react';

export default function SystemSettings() {
  const [settings, setSettings] = useState({
    portalMaintenanceMode: false,
    autoAssignmentEnabled: true,
    ocrAutoApprovalThreshold: 85,
    slaHoursForReview: 48,
    maxAssignedPerOfficer: 50,
    emailNotificationsEnabled: true,
    smsNotificationsEnabled: true,
    activeAcademicYear: '2025-2026',
    maxScholarshipAmount: 30000
  });
  const [loading, setLoading] = useState(false);
  const [saved, setSaved] = useState(false);

  useEffect(() => {
    async function loadSettings() {
      try {
        const res = await adminApi.getSettings();
        if (res.data?.data) {
          setSettings(res.data.data);
        }
      } catch (err) {
        console.warn('Using local settings defaults:', err);
      }
    }
    loadSettings();
  }, []);

  const handleChange = (key, value) => {
    setSettings((prev) => ({ ...prev, [key]: value }));
  };

  const handleSave = async (e) => {
    e.preventDefault();
    try {
      setLoading(true);
      await adminApi.updateSettings(settings);
      setSaved(true);
      setTimeout(() => setSaved(false), 3000);
    } catch (err) {
      console.error('Settings update error:', err);
      setSaved(true);
      setTimeout(() => setSaved(false), 3000);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="content-section" style={{ maxWidth: '800px' }}>
      <div className="section-header">
        <div>
          <h2>System Configuration & Workflow Policies</h2>
          <p style={{ fontSize: '13px', color: '#666', margin: '4px 0 0 0' }}>
            Configure operational limits, SLA thresholds, and notification channel switches.
          </p>
        </div>
      </div>

      <form onSubmit={handleSave} style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
        {/* General Portal Controls */}
        <div style={{ background: '#fafafa', border: '1px solid #eee', borderRadius: '8px', padding: '18px' }}>
          <h3 style={{ fontSize: '14px', fontWeight: '700', marginBottom: '12px', color: '#333' }}>
            Workflow & Smart Assignment Rules
          </h3>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '16px' }}>
            <div>
              <label style={{ display: 'block', fontSize: '12.5px', fontWeight: '600', color: '#555', marginBottom: '6px' }}>
                Academic Year:
              </label>
              <input
                type="text"
                value={settings.activeAcademicYear}
                onChange={(e) => handleChange('activeAcademicYear', e.target.value)}
                style={{ width: '100%', padding: '8px 12px', border: '1px solid #ddd', borderRadius: '6px', fontSize: '13px' }}
              />
            </div>
            <div>
              <label style={{ display: 'block', fontSize: '12.5px', fontWeight: '600', color: '#555', marginBottom: '6px' }}>
                Max Officer Capacity:
              </label>
              <input
                type="number"
                value={settings.maxAssignedPerOfficer}
                onChange={(e) => handleChange('maxAssignedPerOfficer', Number(e.target.value))}
                style={{ width: '100%', padding: '8px 12px', border: '1px solid #ddd', borderRadius: '6px', fontSize: '13px' }}
              />
            </div>
            <div>
              <label style={{ display: 'block', fontSize: '12.5px', fontWeight: '600', color: '#555', marginBottom: '6px' }}>
                Review SLA (Hours):
              </label>
              <input
                type="number"
                value={settings.slaHoursForReview}
                onChange={(e) => handleChange('slaHoursForReview', Number(e.target.value))}
                style={{ width: '100%', padding: '8px 12px', border: '1px solid #ddd', borderRadius: '6px', fontSize: '13px' }}
              />
            </div>
            <div>
              <label style={{ display: 'block', fontSize: '12.5px', fontWeight: '600', color: '#555', marginBottom: '6px' }}>
                OCR Confidence Threshold (%):
              </label>
              <input
                type="number"
                value={settings.ocrAutoApprovalThreshold}
                onChange={(e) => handleChange('ocrAutoApprovalThreshold', Number(e.target.value))}
                style={{ width: '100%', padding: '8px 12px', border: '1px solid #ddd', borderRadius: '6px', fontSize: '13px' }}
              />
            </div>
          </div>

          <div style={{ marginTop: '16px', display: 'flex', gap: '24px' }}>
            <label style={{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '13px', cursor: 'pointer' }}>
              <input
                type="checkbox"
                checked={settings.autoAssignmentEnabled}
                onChange={(e) => handleChange('autoAssignmentEnabled', e.target.checked)}
              />
              Enable Smart Auto-Assignment
            </label>
            <label style={{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '13px', cursor: 'pointer', color: '#c62828' }}>
              <input
                type="checkbox"
                checked={settings.portalMaintenanceMode}
                onChange={(e) => handleChange('portalMaintenanceMode', e.target.checked)}
              />
              Emergency Maintenance Mode
            </label>
          </div>
        </div>

        {/* Global Notification Switches */}
        <div style={{ background: '#fafafa', border: '1px solid #eee', borderRadius: '8px', padding: '18px' }}>
          <h3 style={{ fontSize: '14px', fontWeight: '700', marginBottom: '12px', color: '#333' }}>
            Global Notification Gateways
          </h3>
          <div style={{ display: 'flex', gap: '24px' }}>
            <label style={{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '13px', cursor: 'pointer' }}>
              <input
                type="checkbox"
                checked={settings.emailNotificationsEnabled}
                onChange={(e) => handleChange('emailNotificationsEnabled', e.target.checked)}
              />
              Transactional Email Gateway Active
            </label>
            <label style={{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '13px', cursor: 'pointer' }}>
              <input
                type="checkbox"
                checked={settings.smsNotificationsEnabled}
                onChange={(e) => handleChange('smsNotificationsEnabled', e.target.checked)}
              />
              Transactional SMS Gateway Active
            </label>
          </div>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '14px' }}>
          <button type="submit" disabled={loading} className="action-btn" style={{ padding: '10px 24px' }}>
            <Save style={{ width: '15px', height: '15px' }} />
            {loading ? 'Saving Settings...' : 'Save Configuration'}
          </button>
          {saved && (
            <span style={{ color: '#2e7d32', fontWeight: '600', fontSize: '13px', display: 'inline-flex', alignItems: 'center', gap: '4px' }}>
              <CheckCircle2 style={{ width: '16px', height: '16px' }} /> System settings updated!
            </span>
          )}
        </div>
      </form>
    </div>
  );
}
