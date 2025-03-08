loggin-starter

configuration properties example:
```
logging:
    web-request-body-masked-props: .body.active;.body.page.size
    execution-log-enabled: true
    web-request:
        log-enabled: true
        body:
            log-enabled: true
```