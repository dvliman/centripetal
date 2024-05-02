.PHONY: server
server:
	clj -M -m centripetal.main

.PHONY: test
test:
	clj -M:test

.PHONY: docker
docker:
	docker compose up
