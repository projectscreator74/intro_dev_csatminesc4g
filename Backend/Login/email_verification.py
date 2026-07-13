from flask import Flask
from flask import request
from Backend.Login.send_verification import send_verification_email
import json
import random
import time

app = Flask(__name__)

CODE_EXPIRATION_TIME = 900

@app.after_request

def add_cors_headers(response):
    response.headers["Access-Control-Allow-Origin"] = "*"
    response.headers["Access-Control-Allow-Methods"] = "POST, OPTIONS"
    response.headers["Access-Control-Allow-Headers"] = "Content-Type"
    return response

verification_data = {}

@app.route("/start_verification", methods=["POST"])

def start_verification():
    data = request.get_json()
    email = data["email"]

    return email

def create_verification_code(email):
    code = f"{random.randint(0, 999999):06d}"
    verification_data[email] = {"code": code, "expiration": time.time() + CODE_EXPIRATION_TIME, "attempts": 0}
    send_verification_email(email, code)
    return f"code sent: {code}"

@app.route("/send_code", methods=["POST"])

def send_code():
    data = request.get_json()
    email = data["email"]
    result = create_verification_code(email)
    print(verification_data)
    return result

@app.route("/verify_code", methods=["POST"])

def verify_code():
    data = request.get_json()
    email = data["email"]

    submitted_code = data["code"]
    result = check_code(email, submitted_code)
    return result

def check_code(email, submitted_code):
    if email not in verification_data:
        return "no_code_sent"

    stored = verification_data[email]

    if stored["attempts"] >= 5:
        return "too_many_attempts"

    if time.time() > stored["expiration"]:
        return "expired"

    if submitted_code != stored["code"]:
        stored["attempts"] += 1
        return "wrong_code"

    return "success"

app.run(port=5050)