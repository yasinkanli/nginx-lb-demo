# nginx-lb

Nginx ile yük dengeleme (load balancing) örneği. Aynı Spring Boot uygulamasının 4 kopyası Docker Compose ile ayağa kalkar ve önlerindeki Nginx istekleri bu kopyalar arasında dağıtır.

## Mimari

```
                      ┌─> app1:8080
İstemci ─> nginx:80 ──┼─> app2:8080
 (localhost:8080)     ├─> app3:8080
                      └─> app4:8080
```

- **Uygulama:** Spring Boot 4.1.2, Java 21. `GET /hello` isteğine `Response Instance: <INSTANCE_NAME>` döner. Böylece isteği hangi kopyanın karşıladığı görülür.
- **Nginx:** `least_conn` stratejisiyle, yani o an en az aktif bağlantısı olan kopyaya yönlendirir. Ayar dosyası `nginx/nginx.conf`.
- **Docker Compose:** `app1`..`app4` servisleri aynı imajdan, farklı `INSTANCE_NAME` değeriyle çalışır. Nginx host üzerinde `8080` portunu dinler.

## Gereksinimler

- Java 21 veya üstü (Maven Wrapper ile birlikte gelir, ayrıca Maven kurmanız gerekmez)
- Docker ve Docker Compose

## Çalıştırma

1. Uygulamayı paketleyin. `src/Dockerfile`, jar dosyasını `target/` klasöründen alır:

   ```
   ./mvnw package -DskipTests
   ```

2. Tüm servisleri başlatın:

   ```
   docker compose up --build
   ```

3. Denemek için:

   ```
   curl http://localhost:8080/hello
   ```

   Çıktı `Response Instance: app1` gibi görünür. Birkaç kez tekrarlayın, farklı kopyalardan cevap geldiğini görürsünüz.

Durdurmak için `docker compose down`.

## Dağılımı ölçmek

`LoadBalancerClient`, `http://localhost:8080/hello` adresine 20 istek atar ve hangi kopyanın kaç isteğe cevap verdiğini yazdırır. Compose ayaktayken IDE'den `LoadBalancerClient` sınıfının `main` metodunu çalıştırın.

Örnek çıktı:

```
#1 -> Response Instance: app1
...
Distribution:
Response Instance: app1 : 5 request
Response Instance: app2 : 5 request
Response Instance: app3 : 5 request
Response Instance: app4 : 5 request
```

İstekler tek tek ve ardışık gönderildiği için `least_conn` pratikte hemen hemen eşit dağılım verir.

## Uygulamayı Docker olmadan çalıştırma

```
./mvnw spring-boot:run
```

`INSTANCE_NAME` tanımlı değilse uygulama `local` adıyla cevap verir. Bu modda uygulama Spring Boot'un varsayılan portunda, `8080`'de dinler.

## Proje yapısı

```
.
├── docker-compose.yml          # 4 uygulama + nginx
├── nginx/nginx.conf            # upstream ve proxy ayarı
├── pom.xml
└── src
    ├── Dockerfile
    └── main/java/com/yasinkanli/microservice/nginxlb
        ├── NginxLbApplication.java
        ├── controller/HelloController.java      # GET /hello
        └── client/LoadBalancerClient.java       # dağılım test istemcisi
```
