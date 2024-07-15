# Base image
FROM ubuntu:latest as base

# Install required packages
RUN apt-get update && apt-get install -y \
    build-essential \
    cmake \
    libboost-dev \
    libexpat1-dev \
    zlib1g-dev \
    libbz2-dev \
    python3 \
    python3-pip \
    curl \
    wget \
    git \
    vim \
    nano \
    less \
    apt-utils \
    ca-certificates \
    libssl-dev \
    libffi-dev \
    python3-dev

RUN pip3 install --upgrade pip
RUN pip3 install osmium elasticsearch requests

WORKDIR /app

COPY . /app

CMD ["bash"]
