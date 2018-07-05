# MySQL-Front 5.1  (Build 4.13)

/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE */;
/*!40101 SET SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES */;
/*!40103 SET SQL_NOTES='ON' */;


# Host: localhost    Database: berp_db
# ------------------------------------------------------
# Server version 5.5.54

#
# Source for table material_attach
#

CREATE TABLE `material_attach` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `material_id` int(11) DEFAULT '0' COMMENT '必须设为允许空，因为hibernate的inverse为false，会先update set material_id=null，cascade=all再delete',
  `url` varchar(255) DEFAULT '',
  `location` varchar(255) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `priority` int(11) DEFAULT '0',
  `src` text,
  PRIMARY KEY (`Id`),
  KEY `material_id` (`material_id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8;

#
#  Foreign keys for table material_attach
#

ALTER TABLE `material_attach`
ADD CONSTRAINT `material_attach_ibfk_1` FOREIGN KEY (`material_id`) REFERENCES `material` (`id`);


/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
