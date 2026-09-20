import React from 'react';
import { useParams, Link } from 'react-router-dom';
import { ArrowLeft, CheckCircle2, Printer, CreditCard, ShieldCheck } from 'lucide-react';

export default function PaymentDetail() {
  const { id } = useParams();

  const handlePrint = () => {
    window.print();
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
        <Link
          to="/finance/payments"
          style={{ display: 'inline-flex', alignItems: 'center', gap: '6px', color: '#555', textDecoration: 'none', fontSize: '13px', fontWeight: '600' }}
        >
          <ArrowLeft style={{ width: '16px', height: '16px' }} />
          Back to Payments
        </Link>
        <button onClick={handlePrint} className="action-btn">
          <Printer style={{ width: '15px', height: '15px' }} />
          Print Voucher
        </button>
      </div>

      <div className="content-section" style={{ maxWidth: '800px', margin: '0 auto' }}>
        <div className="section-header" style={{ borderBottom: '2px solid #eee', paddingBottom: '16px' }}>
          <div>
            <h2>Payment Voucher: {id}</h2>
            <p style={{ fontSize: '13px', color: '#666', margin: '4px 0 0 0' }}>
              Direct Benefit Transfer (DBT) Confirmation Receipt
            </p>
          </div>
          <span className="status-badge status-approved">
            DISBURSED
          </span>
        </div>

        <table className="applications-table" style={{ marginBottom: '24px' }}>
          <tbody>
            <tr>
              <td style={{ width: '30%', fontWeight: '600', background: '#fcfcfc' }}>Transaction ID:</td>
              <td><code>TXN-{id}-PFMS</code></td>
            </tr>
            <tr>
              <td style={{ fontWeight: '600', background: '#fcfcfc' }}>Amount Disbursed:</td>
              <td><strong style={{ color: '#2e7d32', fontSize: '18px' }}>₹30,000.00</strong></td>
            </tr>
            <tr>
              <td style={{ fontWeight: '600', background: '#fcfcfc' }}>Beneficiary Name:</td>
              <td>Ajay Kumar</td>
            </tr>
            <tr>
              <td style={{ fontWeight: '600', background: '#fcfcfc' }}>Bank Account:</td>
              <td>State Bank of India (A/C: XXXXXXXX9814)</td>
            </tr>
            <tr>
              <td style={{ fontWeight: '600', background: '#fcfcfc' }}>IFSC Code:</td>
              <td>SBIN0001234</td>
            </tr>
            <tr>
              <td style={{ fontWeight: '600', background: '#fcfcfc' }}>Payment Gateway:</td>
              <td>Public Financial Management System (PFMS - DBT)</td>
            </tr>
            <tr>
              <td style={{ fontWeight: '600', background: '#fcfcfc' }}>Settlement Date:</td>
              <td>{new Date().toLocaleDateString()}</td>
            </tr>
          </tbody>
        </table>

        <div style={{ background: '#e8f5e9', border: '1px solid #c8e6c9', borderRadius: '8px', padding: '16px', display: 'flex', alignItems: 'center', gap: '12px' }}>
          <CheckCircle2 style={{ width: '24px', height: '24px', color: '#2e7d32', flexShrink: 0 }} />
          <div style={{ fontSize: '13px', color: '#2e7d32' }}>
            <strong>Payment Cleared:</strong> Funds have been electronically wired to the student's Aadhaar-seeded bank account.
          </div>
        </div>
      </div>
    </div>
  );
}
