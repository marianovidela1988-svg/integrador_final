-- MariaDB dump 10.19  Distrib 10.4.28-MariaDB, for osx10.10 (x86_64)
--
-- Host: localhost    Database: proyecto_final
-- ------------------------------------------------------
-- Server version	10.4.28-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `pedidos`
--

DROP TABLE IF EXISTS `pedidos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pedidos` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `chat_id` varchar(255) DEFAULT NULL,
  `cliente_nombre` varchar(255) DEFAULT NULL,
  `estado` varchar(255) DEFAULT NULL,
  `fecha_hora` varchar(255) DEFAULT NULL,
  `total` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedidos`
--

LOCK TABLES `pedidos` WRITE;
/*!40000 ALTER TABLE `pedidos` DISABLE KEYS */;
INSERT INTO `pedidos` VALUES (1,'7073036890','Nacho Juarez','CONFIRMADO','2026-09-11T17:14:34',1300),(2,'7073036890','Nacho Juarez','CONFIRMADO','2026-09-11T17:15:02',2800),(3,'7073036890','Nacho Juarez','CONFIRMADO','2026-09-11T17:15:35',2600),(4,'7073036890','Nacho Juarez','CONFIRMADO','2026-09-11T17:16:06',950),(5,'7073036890','Nacho Juarez','CONFIRMADO','2026-09-11T17:16:41',3200),(6,'7073036890','Nacho Juarez','CONFIRMADO','2026-09-11T17:17:14',1300),(7,'7073036890','Nacho Juarez','CONFIRMADO','2026-09-11T17:17:43',2700),(8,'7073036890','Nacho Juarez','CONFIRMADO','2026-09-11T17:18:10',450),(9,'7073036890','Nacho Juarez','CONFIRMADO','2026-09-11T17:18:42',1500),(10,'7073036890','Nacho Juarez','CONFIRMADO','2026-09-11T17:19:16',1200),(11,'7073036890','Nacho Juarez','CONFIRMADO','2026-09-11T17:19:53',1200),(12,'7073036890','Nacho Juarez','CONFIRMADO','2026-09-11T17:20:25',700),(13,'7073036890','Nacho Juarez','CONFIRMADO','2026-09-11T17:20:51',1900),(14,'7073036890','Nacho Juarez','CONFIRMADO','2026-09-11T17:21:56',15000),(15,'7073036890','Nacho Juarez','CONFIRMADO','2026-09-11T17:23:11',1950),(16,'7073036890','Nacho Juarez','CONFIRMADO','2026-09-11T17:24:19',4100),(17,'7073036890','Nacho Juarez','CANCELADO','2026-09-11T17:24:54',7200),(18,'7073036890','Nacho Juarez','CANCELADO','2026-09-11T17:25:40',4200),(19,'7073036890','Nacho Juarez','CANCELADO','2026-09-11T17:26:10',4800),(20,'7073036890','Nacho Juarez','CONFIRMADO','2026-09-11T17:26:43',32000);
/*!40000 ALTER TABLE `pedidos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `items_pedido`
--

DROP TABLE IF EXISTS `items_pedido`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `items_pedido` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cantidad` int(11) DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `precio` double DEFAULT NULL,
  `producto_id` bigint(20) DEFAULT NULL,
  `subtotal` double DEFAULT NULL,
  `pedido_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKhshdf36fo6kflmxhom10pbn39` (`pedido_id`),
  CONSTRAINT `FKhshdf36fo6kflmxhom10pbn39` FOREIGN KEY (`pedido_id`) REFERENCES `pedidos` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `items_pedido`
--

LOCK TABLES `items_pedido` WRITE;
/*!40000 ALTER TABLE `items_pedido` DISABLE KEYS */;
INSERT INTO `items_pedido` VALUES (1,1,'Coca-Cola Regular',1300,1,1300,1),(2,1,'Marlboro Box',2800,7,2800,2),(3,1,'Chocolate Aireado Milka',2600,9,2600,3),(4,1,'Jabon Dove',950,13,950,4),(5,1,'Doritos 250gr',3200,10,3200,5),(6,1,'Coca-Cola Zero',1300,2,1300,6),(7,1,'Phillips-Morris Convertible',2700,8,2700,7),(8,1,'Bombon Bon o Bon',450,11,450,8),(9,1,'Pasta de dientes Colgate',1500,14,1500,9),(10,1,'Pepsi Regular',1200,3,1200,10),(11,1,'Sprite Regular',1200,4,1200,11),(12,1,'Gomitas Mogul',700,12,700,12),(13,1,'Cerveza Quilmes',1900,5,1900,13),(14,1,'Coca-Cola Regular',1300,1,1300,14),(15,1,'Fernet Branca',10500,6,10500,14),(16,1,'Doritos 250gr',3200,10,3200,14),(17,1,'Gomitas Mogul',700,12,700,15),(18,1,'Leche descremada LS',1250,17,1250,15),(19,1,'Chocolate Aireado Milka',2600,9,2600,16),(20,1,'Pasta de dientes Colgate',1500,14,1500,16),(21,3,'Queso crema La Serenisima',2400,15,7200,17),(22,2,'Manteca La Serenisima',2100,16,4200,18),(23,4,'Sprite Regular',1200,4,4800,19),(24,10,'Doritos 250gr',3200,10,32000,20);
/*!40000 ALTER TABLE `items_pedido` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `productos`
--

DROP TABLE IF EXISTS `productos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `productos` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(255) DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `precio` double DEFAULT NULL,
  `stock` int(11) DEFAULT NULL,
  `categoria_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK2fwq10nwymfv7fumctxt9vpgb` (`categoria_id`),
  CONSTRAINT `FK2fwq10nwymfv7fumctxt9vpgb` FOREIGN KEY (`categoria_id`) REFERENCES `categorias` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `productos`
--

LOCK TABLES `productos` WRITE;
/*!40000 ALTER TABLE `productos` DISABLE KEYS */;
INSERT INTO `productos` VALUES (1,'Gaseosa cola 500 ml','Coca-Cola Regular',1300,22,1),(2,'Gaseosa cola sin azucar 500 ml','Coca-Cola Zero',1300,48,1),(3,'Gaseosa cola 500 ml','Pepsi Regular',1200,11,1),(4,'Gaseosa lima-limon 500 ml','Sprite Regular',1200,2,1),(5,'Cerveza rubia 473 ml','Cerveza Quilmes',1900,20,1),(6,'Aperitivo 750 ml','Fernet Branca',10500,11,1),(7,'Cigarrillos 20 u','Marlboro Box',2800,29,2),(8,'Cigarrillos 20 u','Phillips-Morris Convertible',2700,21,2),(9,'Tableta 100 g','Chocolate Aireado Milka',2600,19,3),(10,'Snack de maiz 250 g','Doritos 250gr',3200,27,3),(11,'Bombon de mani 15 g','Bombon Bon o Bon',450,43,3),(12,'Gomitas frutales 40 g','Gomitas Mogul',700,8,3),(13,'Jabon de tocador 90 g','Jabon Dove',950,13,4),(14,'Crema dental 90 g','Pasta de dientes Colgate',1500,45,4),(15,'Queso untable 290 g','Queso crema La Serenisima',2400,2,5),(16,'Pan de manteca 200 g','Manteca La Serenisima',2100,1,5),(17,'Leche descremada sachet 1 L','Leche descremada LS',1250,27,5);
/*!40000 ALTER TABLE `productos` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-11 17:33:35
