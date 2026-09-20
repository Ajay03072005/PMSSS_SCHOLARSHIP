# EmailJS Email Templates for PMSSS Scholarship Portal

## 📧 Template 1: Registration Welcome Email

### Template Settings
- **Template Name**: `Registration Welcome`
- **From Name**: `PMSSS Scholarship Portal`
- **Subject**: `Welcome to PMSSS Scholarship Portal - Registration Successful`
- **Reply To**: `{{user_email}}`

### Template Variables Used
- `{{to_name}}` - User's full name
- `{{user_email}}` - User's email address
- `{{registration_date}}` - Date of registration
- `{{login_url}}` - Login page URL
- `{{apply_url}}` - Application form URL
- `{{portal_url}}` - Main portal URL

### Email Body (HTML)
```html
<div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; background-color: #f5f5f5;">
  <!-- Header -->
  <div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; text-align: center; border-radius: 10px 10px 0 0;">
    <h1 style="color: white; margin: 0; font-size: 32px; font-weight: bold;">
      <span style="color: #ffd700;">P</span><span style="color: #ff69b4;">M</span><span style="color: #87ceeb;">SSS</span>
    </h1>
    <p style="color: rgba(255,255,255,0.9); margin: 10px 0 0 0; font-size: 14px; letter-spacing: 2px;">SCHOLARSHIP PORTAL</p>
  </div>
  
  <!-- Body -->
  <div style="padding: 40px 30px; background: white;">
    <h2 style="color: #667eea; margin: 0 0 20px 0; font-size: 26px;">Welcome to PMSSS! 🎉</h2>
    
    <p style="color: #333; font-size: 16px; line-height: 1.6; margin: 15px 0;">Dear <strong>{{to_name}}</strong>,</p>
    
    <p style="color: #333; font-size: 16px; line-height: 1.6; margin: 15px 0;">
      Congratulations! Your account has been successfully created on the <strong>Prime Minister's Special Scholarship Scheme (PMSSS)</strong> Portal.
    </p>
    
    <!-- Registration Details Box -->
    <div style="background: linear-gradient(135deg, #e3f2fd 0%, #f3e5f5 100%); border-left: 4px solid #2196f3; padding: 20px; margin: 25px 0; border-radius: 5px;">
      <p style="margin: 0; color: #0d47a1; font-size: 15px; font-weight: bold;">📋 Registration Details</p>
      <p style="margin: 10px 0 5px 0; color: #1565c0; font-size: 14px;">
        <strong>Email:</strong> {{user_email}}
      </p>
      <p style="margin: 5px 0 0 0; color: #1565c0; font-size: 14px;">
        <strong>Registration Date:</strong> {{registration_date}}
      </p>
    </div>
    
    <!-- Next Steps -->
    <h3 style="color: #667eea; margin: 30px 0 15px 0; font-size: 20px;">🎯 Next Steps</h3>
    <ol style="color: #555; font-size: 15px; line-height: 1.9; padding-left: 20px;">
      <li><strong style="color: #333;">Complete Your Application</strong> - Fill out the scholarship application form with accurate details</li>
      <li><strong style="color: #333;">Upload Documents</strong> - Prepare all necessary documents (Aadhar Card, Income Certificate, Marksheets, etc.)</li>
      <li><strong style="color: #333;">Submit Application</strong> - Review your information carefully and submit for verification</li>
      <li><strong style="color: #333;">Track Status</strong> - Monitor your application progress through the portal</li>
    </ol>
    
    <!-- Action Buttons -->
    <div style="text-align: center; margin: 35px 0;">
      <a href="{{apply_url}}" style="display: inline-block; background-color: #e91e63; color: white; padding: 14px 35px; text-decoration: none; border-radius: 25px; font-weight: bold; font-size: 15px; margin: 8px; box-shadow: 0 4px 15px rgba(233, 30, 99, 0.3);">
        📝 Start Application
      </a>
      <a href="{{login_url}}" style="display: inline-block; background-color: #667eea; color: white; padding: 14px 35px; text-decoration: none; border-radius: 25px; font-weight: bold; font-size: 15px; margin: 8px; box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);">
        🔐 Login to Portal
      </a>
    </div>
    
    <!-- Important Notice -->
    <div style="background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 25px 0; border-radius: 5px;">
      <p style="margin: 0; color: #856404; font-size: 14px; line-height: 1.6;">
        <strong>⚠️ Important:</strong> Keep your login credentials secure and confidential. Never share your password with anyone. PMSSS officials will never ask for your password.
      </p>
    </div>
    
    <!-- Help Section -->
    <div style="background-color: #f8f9fa; padding: 20px; border-radius: 5px; margin: 25px 0;">
      <h4 style="color: #667eea; margin: 0 0 10px 0; font-size: 16px;">📞 Need Help?</h4>
      <p style="color: #666; font-size: 14px; line-height: 1.6; margin: 0;">
        If you have any questions or need assistance, feel free to contact our support team at 
        <a href="mailto:support@pmsss.gov.in" style="color: #e91e63; text-decoration: none; font-weight: bold;">support@pmsss.gov.in</a>
        or call our helpline: <strong>1800-XXX-XXXX</strong>
      </p>
    </div>
    
    <!-- Regards -->
    <p style="color: #333; font-size: 15px; line-height: 1.6; margin: 30px 0 10px 0;">
      Best Regards,<br>
      <strong>PMSSS Team</strong><br>
      <span style="color: #666; font-size: 13px;">Government of India</span>
    </p>
  </div>
  
  <!-- Footer -->
  <div style="background-color: #2c3e50; padding: 25px; text-align: center; border-radius: 0 0 10px 10px;">
    <p style="margin: 0 0 10px 0; color: rgba(255,255,255,0.8); font-size: 13px;">
      © 2025 PMSSS Scholarship Portal. All rights reserved.
    </p>
    <p style="margin: 0; color: rgba(255,255,255,0.6); font-size: 12px;">
      Government of India | Ministry of Education
    </p>
    <div style="margin-top: 15px;">
      <a href="{{portal_url}}" style="color: #e91e63; text-decoration: none; font-size: 13px; margin: 0 10px;">Visit Portal</a>
      <span style="color: rgba(255,255,255,0.3);">|</span>
      <a href="mailto:support@pmsss.gov.in" style="color: #e91e63; text-decoration: none; font-size: 13px; margin: 0 10px;">Contact Support</a>
    </div>
  </div>
</div>
```

---

## ✅ Template 2: Application Confirmation Email

### Template Settings
- **Template Name**: `Application Confirmation`
- **From Name**: `PMSSS Scholarship Portal`
- **Subject**: `Application Submitted Successfully - {{application_id}}`
- **Reply To**: `{{to_email}}`

### Template Variables Used
- `{{to_name}}` - Applicant's full name
- `{{to_email}}` - Applicant's email
- `{{application_id}}` - Unique application ID
- `{{submission_date}}` - Date and time of submission
- `{{status}}` - Application status (Submitted)
- `{{track_url}}` - Status tracking page URL

### Email Body (HTML)
```html
<div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; background-color: #f5f5f5;">
  <!-- Header -->
  <div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; text-align: center; border-radius: 10px 10px 0 0;">
    <h1 style="color: white; margin: 0; font-size: 32px; font-weight: bold;">
      <span style="color: #ffd700;">P</span><span style="color: #ff69b4;">M</span><span style="color: #87ceeb;">SSS</span>
    </h1>
    <p style="color: rgba(255,255,255,0.9); margin: 10px 0 0 0; font-size: 14px; letter-spacing: 2px;">SCHOLARSHIP PORTAL</p>
  </div>
  
  <!-- Success Banner -->
  <div style="background: linear-gradient(135deg, #4caf50 0%, #45a049 100%); padding: 20px; text-align: center;">
    <h2 style="color: white; margin: 0; font-size: 24px;">✅ Application Submitted Successfully!</h2>
  </div>
  
  <!-- Body -->
  <div style="padding: 40px 30px; background: white;">
    <p style="color: #333; font-size: 16px; line-height: 1.6; margin: 0 0 20px 0;">Dear <strong>{{to_name}}</strong>,</p>
    
    <p style="color: #333; font-size: 16px; line-height: 1.6; margin: 15px 0;">
      Thank you for submitting your application for the <strong>Prime Minister's Special Scholarship Scheme (PMSSS)</strong>. 
      Your application has been received successfully and is now under review.
    </p>
    
    <!-- Application Details Table -->
    <table style="width: 100%; margin: 30px 0; border-collapse: collapse; border: 2px solid #e0e0e0; border-radius: 8px; overflow: hidden;">
      <tr>
        <td style="padding: 15px; border-bottom: 1px solid #e0e0e0; font-weight: bold; background: linear-gradient(135deg, #f5f5f5 0%, #eeeeee 100%); color: #333; width: 45%;">
          Application ID
        </td>
        <td style="padding: 15px; border-bottom: 1px solid #e0e0e0; color: #e91e63; font-weight: bold; font-size: 18px; background: white;">
          {{application_id}}
        </td>
      </tr>
      <tr>
        <td style="padding: 15px; border-bottom: 1px solid #e0e0e0; font-weight: bold; background: linear-gradient(135deg, #f5f5f5 0%, #eeeeee 100%); color: #333;">
          Status
        </td>
        <td style="padding: 15px; border-bottom: 1px solid #e0e0e0; color: #2196f3; font-weight: bold; background: white;">
          {{status}}
        </td>
      </tr>
      <tr>
        <td style="padding: 15px; font-weight: bold; background: linear-gradient(135deg, #f5f5f5 0%, #eeeeee 100%); color: #333;">
          Submitted On
        </td>
        <td style="padding: 15px; color: #666; background: white;">
          {{submission_date}}
        </td>
      </tr>
    </table>
    
    <!-- Important Notice -->
    <div style="background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 20px; margin: 25px 0; border-radius: 5px;">
      <p style="margin: 0 0 10px 0; color: #856404; font-size: 15px; font-weight: bold;">
        ⚠️ Important Information
      </p>
      <ul style="margin: 10px 0 0 0; padding-left: 20px; color: #856404; font-size: 14px; line-height: 1.8;">
        <li>Save your <strong>Application ID</strong> for future reference</li>
        <li>You can track your application status using this ID</li>
        <li>Keep all original documents ready for verification</li>
        <li>Check your email regularly for updates</li>
      </ul>
    </div>
    
    <!-- What's Next -->
    <div style="background: linear-gradient(135deg, #e3f2fd 0%, #f3e5f5 100%); padding: 20px; margin: 25px 0; border-radius: 8px; border-left: 4px solid #2196f3;">
      <h3 style="color: #1565c0; margin: 0 0 15px 0; font-size: 18px;">📋 What Happens Next?</h3>
      <ol style="color: #333; font-size: 14px; line-height: 1.9; margin: 0; padding-left: 20px;">
        <li><strong>Document Verification</strong> - Our team will verify your submitted documents</li>
        <li><strong>Eligibility Check</strong> - Your application will be reviewed against eligibility criteria</li>
        <li><strong>Status Updates</strong> - You will receive email notifications at each stage</li>
        <li><strong>Final Decision</strong> - Approved applications will be notified for disbursement</li>
      </ol>
    </div>
    
    <!-- Track Application Button -->
    <div style="text-align: center; margin: 35px 0;">
      <a href="{{track_url}}" style="display: inline-block; background-color: #e91e63; color: white; padding: 16px 40px; text-decoration: none; border-radius: 25px; font-weight: bold; font-size: 16px; box-shadow: 0 4px 15px rgba(233, 30, 99, 0.4);">
        📊 Track Application Status
      </a>
    </div>
    
    <!-- Processing Time -->
    <div style="background-color: #f8f9fa; padding: 20px; border-radius: 5px; margin: 25px 0; border: 1px solid #e0e0e0;">
      <h4 style="color: #667eea; margin: 0 0 10px 0; font-size: 16px;">⏱️ Processing Time</h4>
      <p style="color: #666; font-size: 14px; line-height: 1.6; margin: 0;">
        Applications are typically processed within <strong>30-45 days</strong> from the submission date. 
        You can track your application progress anytime using your Application ID.
      </p>
    </div>
    
    <!-- Help Section -->
    <div style="background-color: #f8f9fa; padding: 20px; border-radius: 5px; margin: 25px 0;">
      <h4 style="color: #667eea; margin: 0 0 10px 0; font-size: 16px;">💬 Need Help?</h4>
      <p style="color: #666; font-size: 14px; line-height: 1.6; margin: 0;">
        For any queries or assistance, contact us at:
      </p>
      <p style="color: #333; font-size: 14px; margin: 10px 0 0 0;">
        📧 Email: <a href="mailto:support@pmsss.gov.in" style="color: #e91e63; text-decoration: none; font-weight: bold;">support@pmsss.gov.in</a><br>
        📞 Helpline: <strong>1800-XXX-XXXX</strong> (Mon-Fri, 9 AM - 6 PM)
      </p>
    </div>
    
    <!-- Regards -->
    <p style="color: #333; font-size: 15px; line-height: 1.6; margin: 30px 0 10px 0;">
      Best Regards,<br>
      <strong>PMSSS Team</strong><br>
      <span style="color: #666; font-size: 13px;">Government of India</span>
    </p>
  </div>
  
  <!-- Footer -->
  <div style="background-color: #2c3e50; padding: 25px; text-align: center; border-radius: 0 0 10px 10px;">
    <p style="margin: 0 0 10px 0; color: rgba(255,255,255,0.8); font-size: 13px;">
      © 2025 PMSSS Scholarship Portal. All rights reserved.
    </p>
    <p style="margin: 0; color: rgba(255,255,255,0.6); font-size: 12px;">
      Government of India | Ministry of Education
    </p>
    <div style="margin-top: 15px;">
      <a href="{{track_url}}" style="color: #e91e63; text-decoration: none; font-size: 13px; margin: 0 10px;">Track Status</a>
      <span style="color: rgba(255,255,255,0.3);">|</span>
      <a href="mailto:support@pmsss.gov.in" style="color: #e91e63; text-decoration: none; font-size: 13px; margin: 0 10px;">Contact Support</a>
    </div>
  </div>
</div>
```

---

## 📬 Template 3: Status Update Email (Optional)

### Template Settings
- **Template Name**: `Status Update`
- **From Name**: `PMSSS Scholarship Portal`
- **Subject**: `Application Status Update - {{application_id}}`
- **Reply To**: `support@pmsss.gov.in`

### Template Variables Used
- `{{to_name}}` - Applicant's name
- `{{to_email}}` - Applicant's email
- `{{application_id}}` - Application ID
- `{{status}}` - New status (Under Review/Verified/Approved/Rejected)
- `{{update_date}}` - Date of status update
- `{{remarks}}` - Additional remarks/notes
- `{{portal_url}}` - Portal URL

### Email Body (HTML)
```html
<div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; background-color: #f5f5f5;">
  <!-- Header -->
  <div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; text-align: center; border-radius: 10px 10px 0 0;">
    <h1 style="color: white; margin: 0; font-size: 32px; font-weight: bold;">
      <span style="color: #ffd700;">P</span><span style="color: #ff69b4;">M</span><span style="color: #87ceeb;">SSS</span>
    </h1>
    <p style="color: rgba(255,255,255,0.9); margin: 10px 0 0 0; font-size: 14px; letter-spacing: 2px;">SCHOLARSHIP PORTAL</p>
  </div>
  
  <!-- Status Update Banner -->
  <div style="background: linear-gradient(135deg, #ff9800 0%, #f57c00 100%); padding: 20px; text-align: center;">
    <h2 style="color: white; margin: 0; font-size: 24px;">🔔 Application Status Update</h2>
  </div>
  
  <!-- Body -->
  <div style="padding: 40px 30px; background: white;">
    <p style="color: #333; font-size: 16px; line-height: 1.6; margin: 0 0 20px 0;">Dear <strong>{{to_name}}</strong>,</p>
    
    <p style="color: #333; font-size: 16px; line-height: 1.6; margin: 15px 0;">
      Your PMSSS scholarship application status has been updated. Please find the details below:
    </p>
    
    <!-- Status Details Table -->
    <table style="width: 100%; margin: 30px 0; border-collapse: collapse; border: 2px solid #e0e0e0; border-radius: 8px; overflow: hidden;">
      <tr>
        <td style="padding: 15px; border-bottom: 1px solid #e0e0e0; font-weight: bold; background: linear-gradient(135deg, #f5f5f5 0%, #eeeeee 100%); color: #333; width: 45%;">
          Application ID
        </td>
        <td style="padding: 15px; border-bottom: 1px solid #e0e0e0; color: #e91e63; font-weight: bold; font-size: 16px; background: white;">
          {{application_id}}
        </td>
      </tr>
      <tr>
        <td style="padding: 15px; border-bottom: 1px solid #e0e0e0; font-weight: bold; background: linear-gradient(135deg, #f5f5f5 0%, #eeeeee 100%); color: #333;">
          Current Status
        </td>
        <td style="padding: 15px; border-bottom: 1px solid #e0e0e0; color: #2196f3; font-weight: bold; font-size: 18px; background: white;">
          {{status}}
        </td>
      </tr>
      <tr>
        <td style="padding: 15px; font-weight: bold; background: linear-gradient(135deg, #f5f5f5 0%, #eeeeee 100%); color: #333;">
          Updated On
        </td>
        <td style="padding: 15px; color: #666; background: white;">
          {{update_date}}
        </td>
      </tr>
    </table>
    
    <!-- Remarks Section -->
    <div style="background: linear-gradient(135deg, #e3f2fd 0%, #f3e5f5 100%); border-left: 4px solid #2196f3; padding: 20px; margin: 25px 0; border-radius: 5px;">
      <h4 style="color: #1565c0; margin: 0 0 10px 0; font-size: 16px;">📝 Remarks</h4>
      <p style="margin: 0; color: #333; font-size: 15px; line-height: 1.7;">
        {{remarks}}
      </p>
    </div>
    
    <!-- View Application Button -->
    <div style="text-align: center; margin: 35px 0;">
      <a href="{{portal_url}}" style="display: inline-block; background-color: #e91e63; color: white; padding: 16px 40px; text-decoration: none; border-radius: 25px; font-weight: bold; font-size: 16px; box-shadow: 0 4px 15px rgba(233, 30, 99, 0.4);">
        🔍 View Full Details
      </a>
    </div>
    
    <!-- Help Section -->
    <div style="background-color: #f8f9fa; padding: 20px; border-radius: 5px; margin: 25px 0;">
      <h4 style="color: #667eea; margin: 0 0 10px 0; font-size: 16px;">💬 Need Clarification?</h4>
      <p style="color: #666; font-size: 14px; line-height: 1.6; margin: 0;">
        If you have any questions about this status update, please contact our support team:
      </p>
      <p style="color: #333; font-size: 14px; margin: 10px 0 0 0;">
        📧 Email: <a href="mailto:support@pmsss.gov.in" style="color: #e91e63; text-decoration: none; font-weight: bold;">support@pmsss.gov.in</a><br>
        📞 Helpline: <strong>1800-XXX-XXXX</strong>
      </p>
    </div>
    
    <!-- Regards -->
    <p style="color: #333; font-size: 15px; line-height: 1.6; margin: 30px 0 10px 0;">
      Best Regards,<br>
      <strong>PMSSS Team</strong><br>
      <span style="color: #666; font-size: 13px;">Government of India</span>
    </p>
  </div>
  
  <!-- Footer -->
  <div style="background-color: #2c3e50; padding: 25px; text-align: center; border-radius: 0 0 10px 10px;">
    <p style="margin: 0 0 10px 0; color: rgba(255,255,255,0.8); font-size: 13px;">
      © 2025 PMSSS Scholarship Portal. All rights reserved.
    </p>
    <p style="margin: 0; color: rgba(255,255,255,0.6); font-size: 12px;">
      Government of India | Ministry of Education
    </p>
    <div style="margin-top: 15px;">
      <a href="{{portal_url}}" style="color: #e91e63; text-decoration: none; font-size: 13px; margin: 0 10px;">Visit Portal</a>
      <span style="color: rgba(255,255,255,0.3);">|</span>
      <a href="mailto:support@pmsss.gov.in" style="color: #e91e63; text-decoration: none; font-size: 13px; margin: 0 10px;">Contact Support</a>
    </div>
  </div>
</div>
```

---

## 🔧 How to Use These Templates in EmailJS

### Step 1: Create Template in EmailJS
1. Login to EmailJS dashboard
2. Go to **Email Templates**
3. Click **Create New Template**
4. Copy the entire HTML code from above
5. Paste it in the **Content** section

### Step 2: Configure Template Settings
- Set the **Template Name** as mentioned above
- Set the **Subject** as mentioned above
- Make sure all **{{variables}}** are preserved exactly as shown

### Step 3: Test Template
1. Use the **Test** button in EmailJS
2. Fill in sample values for all variables
3. Send test email to verify formatting

### Step 4: Update Configuration
Update `js/emailService.js` with your template IDs:
```javascript
const EMAILJS_CONFIG = {
    PUBLIC_KEY: 'your_public_key',
    SERVICE_ID: 'your_service_id',
    TEMPLATE_ID_REGISTRATION: 'template_reg123',      // Registration template ID
    TEMPLATE_ID_CONFIRMATION: 'template_app456',      // Application template ID
    TEMPLATE_ID_STATUS_UPDATE: 'template_status789'   // Status update template ID
};
```

---

## ✅ Template Variables Mapping

| JavaScript Variable | EmailJS Template Variable | Description |
|-------------------|-------------------------|-------------|
| `userData.fullName` | `{{to_name}}` | Full name of user |
| `userData.email` | `{{user_email}}` | User's email address |
| `applicationData.email` | `{{to_email}}` | Applicant's email |
| `applicationData.firstName + lastName` | `{{to_name}}` | Applicant's full name |
| `applicationData.applicationId` | `{{application_id}}` | Unique application ID |
| `new Date().toLocaleDateString()` | `{{submission_date}}` | Date formatted |
| `statusData.status` | `{{status}}` | Application status |
| `statusData.remarks` | `{{remarks}}` | Admin remarks |
| `window.location.origin + '/login.html'` | `{{login_url}}` | Login page URL |
| `window.location.origin + '/apply.html'` | `{{apply_url}}` | Apply page URL |
| `window.location.origin + '/status.html'` | `{{track_url}}` | Track status URL |
| `window.location.origin` | `{{portal_url}}` | Portal home URL |

---

## 📝 Notes

- All templates are mobile-responsive
- Templates use inline CSS for maximum email client compatibility
- PMSSS branding colors maintained throughout
- Professional government portal design
- All links are dynamic and will work in any environment (localhost or production)

🎉 **Ready to use!** Just copy, paste, and configure!
