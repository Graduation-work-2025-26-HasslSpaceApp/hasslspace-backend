FROM openresty/openresty:latest

RUN apt-get update \
 && apt-get install -y luarocks \
 && luarocks install lua-resty-jwt