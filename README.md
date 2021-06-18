# CSD_PA2_G05
PA2 CSD

O nome do repositório não corresponde à versão correta.
O projeto foi implementado e executado num ambiente linux ubuntu 

# Regras para Executar o PA2:

1 - Abrir a diretoria do projeto na Command Line.


2 - Entrar na diretoria PA1 (para correr o Server)

2.1 - Entrar na diretoria Client (para correr o cliente)
# Como Compilar:

mvn clean package



# Inicializar a base de dados: 


sudo docker run -d -p 27017:27017 --name mongodb1 mongo:latest\
sudo docker run -d -p 27018:27017 --name mongodb2 mongo:latest\
sudo docker run -d -p 27019:27017 --name mongodb3 mongo:latest\
sudo docker run -d -p 27020:27017 --name mongodb4 mongo:latest


# Como executar o código do lado do Servidor:


Inicializar as replicas:

    Em cada terminal:

java -jar target/[NOME DA PACKAGE]  --replica.id=0 --server.port=8444 --spring.data.mongodb.port=27017\

java -jar target/[NOME DA PACKAGE]  --replica.id=1 --server.port=8445 --spring.data.mongodb.port=27018\

java -jar target/[NOME DA PACKAGE]  --replica.id=2 --server.port=8446 --spring.data.mongodb.port=27019\

java -jar target/[NOME DA PACKAGE]  --replica.id=3 --server.port=8447 --spring.data.mongodb.port=27020


# Como executar o Cliente:

java -jar target/[NOME DA PACKAGE] --token=<input String>

Para ver os comandos disponiveis escrever help na consola
