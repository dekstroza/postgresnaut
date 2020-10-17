FROM oracle/graalvm-ce:20.2.0-java11 as graalvm
WORKDIR /opt
RUN mkdir /opt/bundle

RUN curl -O https://musl.libc.org/releases/musl-1.2.1.tar.gz && \
    curl -O https://zlib.net/zlib-1.2.11.tar.gz && \
    tar zxvf musl-1.2.1.tar.gz && \
	tar zxvf zlib-1.2.11.tar.gz && \
	cd /opt/musl-1.2.1 && \
    ./configure --disable-shared --prefix=/opt/bundle && \
    make && \
	make install && \
	export PATH=$PATH:/opt/bundle/bin && \
    cd /opt/zlib-1.2.11 && \
    export CC=musl-gcc && \
    ./configure --static --prefix=/opt/bundle && \
    make && \
	make install
COPY libstdc++.a /opt/bundle/lib/
ENV PATH=$PATH:/opt/bundle/bin
RUN gu install native-image

