# EmailJS Setup Guide for PMSSS Scholarship Portal

## Step 1: Create EmailJS Account

1. Go to [https://www.emailjs.com/](https://www.emailjs.com/)
2. Click **"Sign Up"** (It's free - 200 emails/month)
3. Verify your email address

## Step 2: Add Email Service

1. Go to **"Email Services"** in the dashboard
2. Click **"Add New Service"**
3. Choose **Gmail** (recommended) or any other service
4. Connect your Gmail account
5. Copy the **Service ID** (e.g., `service_abc123`)

## Step 3: Create Email Templates

### Template 1: Registration Welcome Email

1. Go to **"Email Templates"**
2. Click **"Create New Template"**
3. **Template Name**: `Registration Welcome`
4. **Template ID**: Copy this (e.g., `template_reg123`)

**Subject:**
```
Welcome to PMSSS Scholarship Portal - Registration Successful
```

**Content:**
```html
<div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
  <div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; text-align: center; border-radius: 10px 10px 0 0;">
    <h1 style="color: white; margin: 0; font-size: 28px;">
      <span style="color: #ffd700;">P</span><span style="color: #ff69b4;">M</span><span style="color: #87ceeb;">SSS</span>
    </h1>
    <p style="color: rgba(255,255,255,0.9); margin: 5px 0 0 0;">Scholarship Portal</p>
  </div>
  
  <div style="padding: 40px 30px; background: white; color: #333;">
    <h2 style="color: #667eea;">Welcome to PMSSS! 🎉</h2>
    <p>Dear {{to_name}},</p>
    <p>Congratulations! Your account has been successfully created on the PMSSS Scholarship Portal.</p>
    
    <div style="background-color: #e3f2fd; border-left: 4px solid #2196f3; padding: 15px; margin: 20px 0;">
      <p style="margin: 0; color: #0d47a1;"><strong>Registration Details:</strong></p>
      <p style="margin: 10px 0 0 0; color: #0d47a1;">Email: {{user_email}}</p>
      <p style="margin: 5px 0 0 0; color: #0d47a1;">Date: {{registration_date}}</p>
    </div>
    
    <h3 style="color: #667eea; margin-top: 30px;">Next Steps:</h3>
    <ol style="line-height: 1.8; color: #555;">
      <li><strong>Complete Your Application</strong> - Fill out the scholarship application form with all required details</li>
      <li><strong>Upload Documents</strong> - Prepare and upload all necessary documents (Aadhar, Income Certificate, etc.)</li>
      <li><strong>Submit Application</strong> - Review and submit your application for verification</li>
      <li><strong>Track Status</strong> - Monitor your application status through the portal</li>
    </ol>
    
    <div style="text-align: center; margin: 30px 0;">
      <a href="{{apply_url}}" style="background-color: #e91e63; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block; font-weight: bold; margin: 5px;">Start Application</a>
      <a href="{{login_url}}" style="background-color: #667eea; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block; font-weight: bold; margin: 5px;">Login to Portal</a>
    </div>
    
    <div style="background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 20px 0;">
      <p style="margin: 0; color: #856404;"><strong>Important:</strong> Keep your login credentials secure. Never share your password with anyone.</p>
    </div>
    
    <p style="color: #666; font-size: 14px; margin-top: 30px;">
      If you have any questions or need assistance, feel free to contact us at support@pmsss.gov.in
    </p>
  </div>
  
  <div style="background-color: #f8f9fa; padding: 20px; text-align: center; border-top: 1px solid #dee2e6; border-radius: 0 0 10px 10px;">
    <p style="margin: 0; color: #666; font-size: 13px;">
      &copy; 2025 PMSSS Scholarship Portal. All rights reserved.<br>
      Government of India
    </p>
  </div>
</div>
```

### Template 2: Application Confirmation

1. Go to **"Email Templates"**
2. Click **"Create New Template"**
3. **Template Name**: `Application Confirmation`
4. **Template ID**: Copy this (e.g., `template_app456`)

**Subject:**
```
Application Submitted Successfully - {{application_id}}
```

**Content:**
```html
<div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
  <div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; text-align: center; border-radius: 10px 10px 0 0;">
    <h1 style="color: white; margin: 0; font-size: 28px;">
      <span style="color: #ffd700;">P</span><span style="color: #ff69b4;">M</span><span style="color: #87ceeb;">SSS</span>
    </h1>
    <p style="color: rgba(255,255,255,0.9); margin: 5px 0 0 0;">Scholarship Portal</p>
  </div>
  
  <div style="padding: 40px 30px; background: white;">
    <h2 style="color: #667eea;">Application Submitted Successfully!</h2>
    <p>Dear {{to_name}},</p>
    <p>Thank you for submitting your application for the PMSSS Scholarship program. Your application has been received successfully.</p>
    
    <table style="width: 100%; margin: 20px 0; border-collapse: collapse;">
      <tr>
        <td style="padding: 10px; border: 1px solid #ddd; font-weight: bold; background-color: #f5f5f5;">Application ID</td>
        <td style="padding: 10px; border: 1px solid #ddd;">{{application_id}}</td>
      </tr>
      <tr>
        <td style="padding: 10px; border: 1px solid #ddd; font-weight: bold; background-color: #f5f5f5;">Status</td>
        <td style="padding: 10px; border: 1px solid #ddd;">{{status}}</td>
      </tr>
      <tr>
        <td style="padding: 10px; border: 1px solid #ddd; font-weight: bold; background-color: #f5f5f5;">Submitted On</td>
        <td style="padding: 10px; border: 1px solid #ddd;">{{submission_date}}</td>
      </tr>
    </table>
    
    <div style="background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 20px 0;">
      <p style="margin: 0; color: #856404;"><strong>Important:</strong> Please save your Application ID for future reference. You can track your application status using this ID.</p>
    </div>
    
    <div style="text-align: center; margin: 30px 0;">
      <a href="{{track_url}}" style="background-color: #e91e63; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block; font-weight: bold;">Track Application</a>
    </div>
    
    <p style="color: #666; font-size: 14px;">
      If you have any questions, please contact us at support@pmsss.gov.in
    </p>
  </div>
  
  <div style="background-color: #f8f9fa; padding: 20px; text-align: center; border-top: 1px solid #dee2e6; border-radius: 0 0 10px 10px;">
    <p style="margin: 0; color: #666; font-size: 13px;">
      &copy; 2025 PMSSS Scholarship Portal. All rights reserved.<br>
      Government of India
    </p>
  </div>
</div>
```

### Template 2: Status Update (Optional)

1. Create another template
2. **Template Name**: `Status Update`
3. **Template ID**: Copy this

**Subject:**
```
Application Status Update - {{application_id}}
```

**Content:**
```html
<div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
  <div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; text-align: center; border-radius: 10px 10px 0 0;">
    <h1 style="color: white; margin: 0; font-size: 28px;">
      <span style="color: #ffd700;">P</span><span style="color: #ff69b4;">M</span><span style="color: #87ceeb;">SSS</span>
    </h1>
    <p style="color: rgba(255,255,255,0.9); margin: 5px 0 0 0;">Scholarship Portal</p>
  </div>
  
  <div style="padding: 40px 30px; background: white;">
    <h2 style="color: #667eea;">Application Status Update</h2>
    <p>Dear {{to_name}},</p>
    <p>Your application status has been updated.</p>
    
    <table style="width: 100%; margin: 20px 0; border-collapse: collapse;">
      <tr>
        <td style="padding: 10px; border: 1px solid #ddd; font-weight: bold; background-color: #f5f5f5;">Application ID</td>
        <td style="padding: 10px; border: 1px solid #ddd;">{{application_id}}</td>
      </tr>
      <tr>
        <td style="padding: 10px; border: 1px solid #ddd; font-weight: bold; background-color: #f5f5f5;">Current Status</td>
        <td style="padding: 10px; border: 1px solid #ddd;">{{status}}</td>
      </tr>
      <tr>
        <td style="padding: 10px; border: 1px solid #ddd; font-weight: bold; background-color: #f5f5f5;">Updated On</td>
        <td style="padding: 10px; border: 1px solid #ddd;">{{update_date}}</td>
      </tr>
    </table>
    
    <div style="background-color: #e3f2fd; border-left: 4px solid #2196f3; padding: 15px; margin: 20px 0;">
      <p style="margin: 0; color: #0d47a1;">{{remarks}}</p>
    </div>
    
    <div style="text-align: center; margin: 30px 0;">
      <a href="{{portal_url}}" style="background-color: #e91e63; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block; font-weight: bold;">View Application</a>
    </div>
  </div>
  
  <div style="background-color: #f8f9fa; padding: 20px; text-align: center; border-top: 1px solid #dee2e6; border-radius: 0 0 10px 10px;">
    <p style="margin: 0; color: #666; font-size: 13px;">
      &copy; 2025 PMSSS Scholarship Portal. All rights reserved.<br>
      Government of India
    </p>
  </div>
</div>
```

## Step 4: Get Your Public Key

1. Go to **"Account"** → **"General"** in EmailJS dashboard
2. Copy your **Public Key** (looks like: `abcd1234efgh5678`)

## Step 5: Update Configuration

Open `js/emailService.js` and update:

```javascript
const EMAILJS_CONFIG = {
    PUBLIC_KEY: 'YOUR_PUBLIC_KEY_HERE',                     // From Account → General
    SERVICE_ID: 'YOUR_SERVICE_ID_HERE',                     // From Email Services
    TEMPLATE_ID_REGISTRATION: 'YOUR_REGISTRATION_TEMPLATE_ID',    // Registration Welcome template
    TEMPLATE_ID_CONFIRMATION: 'YOUR_CONFIRMATION_TEMPLATE_ID',    // Application Confirmation template
    TEMPLATE_ID_STATUS_UPDATE: 'YOUR_STATUS_UPDATE_TEMPLATE_ID'   // Status Update template (optional)
};
```

## Step 6: Test

### Test Registration Email:
1. Go to registration page
2. Create a new account
3. Check email inbox for welcome message

### Test Application Email:
1. Login to the portal
2. Fill and submit an application
3. Check email inbox for confirmation
4. Check browser console for any errors

## Troubleshooting

### Email not received?
- Check spam/junk folder
- Verify all IDs are correct in `emailService.js`
- Check EmailJS dashboard → Usage to see if email was sent
- Open browser console to check for errors

### Rate Limit?
- Free plan: 200 emails/month
- Upgrade plan if needed at EmailJS dashboard

### Template Variables Not Working?
- Make sure variable names in template match exactly (case-sensitive)
- Variables: `{{to_name}}`, `{{application_id}}`, `{{submission_date}}`, etc.

## Free Tier Limits

- **200 emails/month** (free)
- Upgrade to Personal ($7/month) for 1000 emails
- Or Enterprise for unlimited

## Email Templates Summary

Your portal will send **3 types of emails**:

1. **Registration Welcome Email** 📧
   - Sent when user creates an account
   - Includes: Welcome message, login link, application link
   - Template variables: `{{to_name}}`, `{{user_email}}`, `{{registration_date}}`, `{{login_url}}`, `{{apply_url}}`

2. **Application Confirmation Email** ✅
   - Sent when user submits application
   - Includes: Application ID, submission date, tracking link
   - Template variables: `{{to_name}}`, `{{application_id}}`, `{{submission_date}}`, `{{status}}`, `{{track_url}}`

3. **Status Update Email** 📬
   - Sent when application status changes (optional - for admin use)
   - Includes: Current status, update date, remarks
   - Template variables: `{{to_name}}`, `{{application_id}}`, `{{status}}`, `{{update_date}}`, `{{remarks}}`, `{{portal_url}}`

## Files Modified

- ✅ `js/emailService.js` - Added `sendRegistrationEmail()` function
- ✅ `js/register.js` - Integrated registration email sending
- ✅ `js/application.js` - Already has application email (from previous setup)
- ✅ `register.html` - Added EmailJS SDK script
- ✅ `application-form.html` - Already has EmailJS SDK

---

**All done!** Your scholarship portal now sends beautiful emails for registration and application submission automatically! 🎉
