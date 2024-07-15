from fastapi import FastAPI
import requests
from py_eureka_client import eureka_client
import os

app = FastAPI()

# Eureka 클라이언트 설정
eureka_server = "http://localhost:8761/eureka"
app_name = "route"
instance_port = 8076

eureka_client.init(eureka_server=eureka_server,
                   app_name=app_name,
                   instance_port=instance_port)

# Config 서버에서 설정 가져오기
config_server_url = "http://localhost:8071"
config_label = "master"
config_application = "fastapi-service"
config_profile = "default"

def fetch_config():
    url = f"{config_server_url}/{config_application}/{config_profile}/{config_label}"
    response = requests.get(url)
    if response.status_code == 200:
        return response.json()
    else:
        return {}

config = fetch_config()

@app.get("/")
def read_root():
    return {"message": "Hello World"}

@app.get("/config")
def get_config():
    return config

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="localhost", port=instance_port)
