import React from 'react';
import { Link } from 'react-router-dom';
import { Award, BookOpen, Users, CheckCircle, ArrowRight } from 'lucide-react';

export default function About() {
  return (
    <div style={{ padding: '60px 80px', maxWidth: '1000px', margin: '0 auto' }}>
      <div style={{ textAlign: 'center', marginBottom: '40px' }}>
        <div
          style={{
            display: 'inline-block',
            background: 'rgba(211, 47, 47, 0.15)',
            color: '#d32f2f',
            padding: '6px 18px',
            borderRadius: '20px',
            fontSize: '11px',
            fontWeight: '700',
            letterSpacing: '1px',
            textTransform: 'uppercase',
            marginBottom: '14px'
          }}
        >
          Scheme Overview
        </div>
        <h1 style={{ fontSize: '36px', fontWeight: '900', textTransform: 'uppercase', color: '#fff', letterSpacing: '1px' }}>
          About Prime Minister's Special Scholarship Scheme
        </h1>
        <p style={{ fontSize: '16px', color: 'rgba(255, 255, 255, 0.75)', maxWidth: '750px', margin: '12px auto 0' }}>
          An initiative by the Government of India and AICTE to build capabilities of the youth in Jammu & Kashmir and Ladakh.
        </p>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '24px', marginBottom: '40px' }}>
        <div style={{ background: 'rgba(0, 0, 0, 0.4)', backdropFilter: 'blur(10px)', border: '1px solid rgba(255, 255, 255, 0.1)', borderRadius: '12px', padding: '26px' }}>
          <Award style={{ width: '32px', height: '32px', color: '#d32f2f', marginBottom: '12px' }} />
          <h3 style={{ fontSize: '17px', fontWeight: '700', color: '#fff', marginBottom: '8px' }}>Objective</h3>
          <p style={{ fontSize: '13px', color: 'rgba(255, 255, 255, 0.7)', lineHeight: '1.6', margin: 0 }}>
            To encourage youth from J&K and Ladakh to pursue higher education in premier institutions across India with financial assistance.
          </p>
        </div>

        <div style={{ background: 'rgba(0, 0, 0, 0.4)', backdropFilter: 'blur(10px)', border: '1px solid rgba(255, 255, 255, 0.1)', borderRadius: '12px', padding: '26px' }}>
          <BookOpen style={{ width: '32px', height: '32px', color: '#d32f2f', marginBottom: '12px' }} />
          <h3 style={{ fontSize: '17px', fontWeight: '700', color: '#fff', marginBottom: '8px' }}>Course Streams</h3>
          <p style={{ fontSize: '13px', color: 'rgba(255, 255, 255, 0.7)', lineHeight: '1.6', margin: 0 }}>
            Scholarships covering Engineering & Technology, Pharmacy, Architecture, Nursing, Hotel Management, General Degree, and Medical programs.
          </p>
        </div>

        <div style={{ background: 'rgba(0, 0, 0, 0.4)', backdropFilter: 'blur(10px)', border: '1px solid rgba(255, 255, 255, 0.1)', borderRadius: '12px', padding: '26px' }}>
          <Users style={{ width: '32px', height: '32px', color: '#d32f2f', marginBottom: '12px' }} />
          <h3 style={{ fontSize: '17px', fontWeight: '700', color: '#fff', marginBottom: '8px' }}>Scholarship Benefits</h3>
          <p style={{ fontSize: '13px', color: 'rgba(255, 255, 255, 0.7)', lineHeight: '1.6', margin: 0 }}>
            Direct Benefit Transfer (DBT) covering academic fees directly to institutes and annual maintenance allowance to students.
          </p>
        </div>
      </div>

      <div style={{ textAlign: 'center' }}>
        <Link
          to="/register"
          style={{
            backgroundColor: '#d32f2f',
            color: 'white',
            padding: '12px 30px',
            borderRadius: '6px',
            textDecoration: 'none',
            fontWeight: '700',
            fontSize: '13px',
            letterSpacing: '1px',
            display: 'inline-flex',
            alignItems: 'center',
            gap: '8px',
            boxShadow: '0 4px 15px rgba(211, 47, 47, 0.35)'
          }}
        >
          <span>APPLY FOR SCHOLARSHIP</span>
          <ArrowRight style={{ width: '16px', height: '16px' }} />
        </Link>
      </div>
    </div>
  );
}
