FROM openjdk:17-jdk-alpine AS pre
ARG URL

WORKDIR /app
COPY . /app

RUN apk add --no-cache bash freetype fontconfig ttf-dejavu gcompat tzdata openssl
RUN ./gradlew buildPlugin && ./repo_xml.sh ${URL} > updatePlugins.xml

FROM httpd:2.4 AS final

# generate self signed cert for apache
# see the README for instructions on how to use your own
RUN apt-get update && apt-get install -y openssl && rm -rf /var/lib/apt/lists/*
RUN openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout /usr/local/apache2/conf/server.key \
	-out /usr/local/apache2/conf/server.crt \
	-subj "/C=US/ST=YourState/L=YourCity/O=YourOrganization/CN=localhost"

# enable ssl for apache
RUN sed -i \
		-e 's/^#\(Include .*httpd-ssl.conf\)/\1/' \
		-e 's/^#\(LoadModule .*mod_ssl.so\)/\1/' \
		-e 's/^#\(LoadModule .*mod_socache_shmcb.so\)/\1/' \
		/usr/local/apache2/conf/httpd.conf

# copy plugin and repo xml to public dir
COPY --from=pre /app/build/distributions/*.zip /usr/local/apache2/htdocs/
COPY --from=pre /app/updatePlugins.xml /usr/local/apache2/htdocs/updatePlugins.xml

# configure url redirects
RUN find /usr/local/apache2/htdocs -type f -name "*.zip" \
	-exec basename {} \; | sed 's/\.[^.]*$//' | xargs -I {} sh -c 'echo "Redirect 301 /latest /{}.zip" >> /usr/local/apache2/conf/httpd.conf'
RUN find /usr/local/apache2/htdocs -type f -name "updatePlugins.xml" \
	-exec basename {} \; | sed 's/\.[^.]*$//' | xargs -I {} sh -c 'echo "RedirectMatch 301 ^/(/)?$ /{}.xml" >> /usr/local/apache2/conf/httpd.conf'