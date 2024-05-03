FROM --platform=linux/x86_64 clojure:openjdk-8-tools-deps-1.10.1.727
RUN mkdir /app
WORKDIR /app

COPY deps.edn /app
RUN clj -P

COPY . /app

CMD clj -M -m centripetal.main
