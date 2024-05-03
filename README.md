How to run (if clojure is installed)

```
# make server
clj -P
clj -M -m centripetal.main

```

With docker 

```
# make docker
docker compose up
```
* please use the clojure command
* run into issues with docker
  * jakarta/servlet is outdated (compiles up to 52 - java 8),
    * bumping down wouldn't work with M1 chip 
      * can bundle jar inside but I have limited time this week

Running the tests:

```
❯ make test
clj -M:test

Running tests in #{"test"}

Testing centripetal.http-test

Ran 3 tests containing 17 assertions.
0 failures, 0 errors.
```

**Context:**
* This is the first time I use pedestal and component. I haven't spend much time to learn deeply with the APIs 
and internals.
* Made a few assumptions:
  * the naming GET /indicators slightly throw me off because there is a top-level key called "indicators"
in the compromise document. I assume we are asking for top-level document, not the indicators.
  * for search endpoint, use simple search terms, no syntax to drill down matching tags, indicators, etc
* Happy to walkthrough the code

**Area for improvements:**
* Learn about interceptors i.e to handle catch-all error handling and logging
* query params constraint with schema (validation)
* openapi
 
### Filter Indicators by type

``` 

curl http://localhost:8080/indicators\?type\=FileHash-MD5 | jq .

[
  {
    "description": "",
    "tags": [],
    "revision": 1,
    "extract_source": [],
    "name": "Independence Day greeting campaign delivers Emotet",
    "public": 1,
    "indicators": [
      {
        "indicator": "6c0c7ee1f783a1465d1fcad1b227aa43",
        "description": "",
        "created": "2018-07-09T12:51:49",
        "title": "",
        "content": "",
        "type": "FileHash-MD5",
        "id": 1168913004
      },
      ...
    ]
   }
   ....
]

```

### Get All Indicators

```
curl http://localhost:8080/indicators | jq . 

```

### Get Single Indicator

```
curl http://localhost:8080/indicators/5b3af7d85996b430b393f3d6
{
  "description": "phpMyAdmin honeypot logs from a US /32",
  "tags": [
    "phpMyAdmin",
    "honeypot"
  ],
  "revision": 1,
  "extract_source": [],
  "name": "phpMyAdmin honeypot logs for 2018-07-02",
  "public": 1,
  ....
}
```

### Search Indicators

```
curl -XPOST http://localhost:8080/indicators/search -H "Content-Type: application/json" -d '{"author_name": "marcoramilli"}'
```

