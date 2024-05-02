How to run:

```
# if clojure is installed
clj -P
clj -M -m centripetal.main

# 
```


Filter indicators by type

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

Get all indicators

```
curl -v http://localhost:8080/indicators | jq . 

```

Get single indicator

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

