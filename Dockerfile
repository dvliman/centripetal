FROM clojure:temurin-8-tools-deps

RUN mkdir /app
WORKDIR /app

COPY deps.edn /app
RUN clj -P

COPY . /app

CMD clj -M -m centripetal.main
