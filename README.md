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
* just use the clojure command
* run into issue with docker; jakarta/servlet is outdated, bumping down run into issues with M1 chip 

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
* This is the first time I use pedestal and component. I haven't spend much time understanding the APIs 
and internals.
* The naming GET /indicators slightly throw me off because there is a top-level key called "indicators"
in the compromise document. I made a reasonable assumption that we are searching for the top-level document.
* Happy to walkthrough the code

**Area for improvements:**
* Understand more about inceptors i.e to handle catch-all error handling and logging
* query params constraint with schema
* openapi
 
### Filter indicators by type

``` 

curl -v http://localhost:8080/indicators\?type\=FileHash-MD5 | jq .

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

### Get all indicators

```
curl -v http://localhost:8080/indicators | jq . 

```

### Get single indicator

```
curl -v http://localhost:8080/indicators/5b3af7d85996b430b393f3d6
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

