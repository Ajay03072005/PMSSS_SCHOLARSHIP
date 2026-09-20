import React from 'react';
import { Link } from 'react-router-dom';
import { Sparkles, ArrowRight, ShieldCheck, Search } from 'lucide-react';

export default function Home() {
  return (
    <div style={{ padding: '60px 80px', minHeight: 'calc(100vh - 120px)', display: 'flex', alignItems: 'center' }}>
      <div style={{ maxWidth: '950px' }}>
        {/* Government Initiative Badge */}
        <div
          style={{
            display: 'inline-block',
            background: 'rgba(211, 47, 47, 0.15)',
            color: '#d32f2f',
            padding: '8px 22px',
            borderRadius: '30px',
            fontSize: '12px',
            fontWeight: '700',
            letterSpacing: '1.5px',
            textTransform: 'uppercase',
            border: '1px solid rgba(211, 47, 47, 0.3)',
            marginBottom: '28px'
          }}
        >
          Government of India Initiative • AICTE
        </div>

        {/* Hero Title */}
        <h1
          style={{
            fontSize: '64px',
            fontWeight: '900',
            lineHeight: '1.1',
            marginBottom: '22px',
            letterSpacing: '2px',
            textTransform: 'uppercase',
            color: 'white'
          }}
        >
          PRIME MINISTER<br />
          SPECIAL<br />
          <span style={{ color: '#d32f2f' }}>SCHOLARSHIP</span>
        </h1>

        <p
          style={{
            fontSize: '18px',
            color: 'rgba(255, 255, 255, 0.8)',
            fontWeight: '300',
            marginBottom: '36px',
            letterSpacing: '0.5px'
          }}
        >
          Empowering Students | Building Future | Transforming Lives in J&K and Ladakh
        </p>

        {/* Hero Action Buttons */}
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '16px', marginBottom: '48px' }}>
          <Link
            to="/register"
            style={{
              backgroundColor: '#d32f2f',
              color: 'white',
              padding: '14px 34px',
              borderRadius: '8px',
              textDecoration: 'none',
              fontWeight: '700',
              fontSize: '14px',
              letterSpacing: '1px',
              display: 'inline-flex',
              alignItems: 'center',
              gap: '8px',
              boxShadow: '0 4px 15px rgba(211, 47, 47, 0.4)'
            }}
          >
            <span>APPLY NOW</span>
            <ArrowRight style={{ width: '16px', height: '16px' }} />
          </Link>

          <Link
            to="/status"
            style={{
              backgroundColor: 'rgba(255, 255, 255, 0.1)',
              color: 'white',
              border: '1px solid rgba(255, 255, 255, 0.3)',
              padding: '14px 28px',
              borderRadius: '8px',
              textDecoration: 'none',
              fontWeight: '600',
              fontSize: '14px',
              letterSpacing: '1px',
              display: 'inline-flex',
              alignItems: 'center',
              gap: '8px'
            }}
          >
            <Search style={{ width: '16px', height: '16px' }} />
            <span>TRACK STATUS</span>
          </Link>

          <Link
            to="/login"
            style={{
              backgroundColor: 'transparent',
              color: 'rgba(255, 255, 255, 0.85)',
              border: '1px solid rgba(255, 255, 255, 0.2)',
              padding: '14px 24px',
              borderRadius: '8px',
              textDecoration: 'none',
              fontWeight: '600',
              fontSize: '14px',
              letterSpacing: '1px',
              display: 'inline-flex',
              alignItems: 'center',
              gap: '8px'
            }}
          >
            <ShieldCheck style={{ width: '16px', height: '16px', color: '#d32f2f' }} />
            <span>OFFICER LOGIN</span>
          </Link>
        </div>

        {/* Hero Stats */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '30px', borderTop: '1px solid rgba(255, 255, 255, 0.15)', paddingTop: '30px', maxWidth: '650px' }}>
          <div>
            <div style={{ fontSize: '36px', fontWeight: '800', color: '#ffffff' }}>50,000+</div>
            <div style={{ fontSize: '13px', color: 'rgba(255, 255, 255, 0.7)', textTransform: 'uppercase', letterSpacing: '1px', marginTop: '4px' }}>
              Students Benefited
            </div>
          </div>
          <div>
            <div style={{ fontSize: '36px', fontWeight: '800', color: '#d32f2f' }}>₹30,000</div>
            <div style={{ fontSize: '13px', color: 'rgba(255, 255, 255, 0.7)', textTransform: 'uppercase', letterSpacing: '1px', marginTop: '4px' }}>
              Annual Scholarship
            </div>
          </div>
          <div>
            <div style={{ fontSize: '36px', fontWeight: '800', color: '#ffffff' }}>100+</div>
            <div style={{ fontSize: '13px', color: 'rgba(255, 255, 255, 0.7)', textTransform: 'uppercase', letterSpacing: '1px', marginTop: '4px' }}>
              Partner Institutes
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
