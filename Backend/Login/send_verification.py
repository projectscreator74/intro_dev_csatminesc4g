import smtplib
from email.mime.text import MIMEText

SENDER_EMAIL = "retta.walker@ethereal.email"
SMTP_LOGIN = "retta.walker@ethereal.email"
SMTP_PASSWORD = "qqcpZGafx57g48DvJ7"

def send_verification_email(to_email, code):
    msg = MIMEText(f"Welcome (back) to StudyStack!\n\nYour StudyStack verification code is: {code}\nThis will expire in 15 minutes. Do not share this code with anyone.\n\nHave a great day and happy learning!")
    msg["Subject"] = "Your StudyStack verification code"
    msg["From"] = SENDER_EMAIL
    msg["To"] = to_email

    with smtplib.SMTP("smtp.ethereal.email", 587) as server:
        server.starttls()
        server.login(SMTP_LOGIN, SMTP_PASSWORD)
        server.sendmail(SENDER_EMAIL, to_email, msg.as_string())