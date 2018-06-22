# MySQL-Front 5.1  (Build 4.13)

/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE */;
/*!40101 SET SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES */;
/*!40103 SET SQL_NOTES='ON' */;


# Host: localhost    Database: berp_db
# ------------------------------------------------------
# Server version 5.5.54

#
# Source for table ord_record_record
#

DROP TABLE IF EXISTS `ord_record_record`;
CREATE TABLE `ord_record_record` (
  `purchase_record_id` int(11) NOT NULL DEFAULT '0',
  `sell_record_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`purchase_record_id`,`sell_record_id`),
  KEY `sell_record_id` (`sell_record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
#  Foreign keys for table ord_record_record
#

ALTER TABLE `ord_record_record`
ADD CONSTRAINT `ord_record_record_ibfk_1` FOREIGN KEY (`purchase_record_id`) REFERENCES `ord_record` (`Id`),
ADD CONSTRAINT `ord_record_record_ibfk_2` FOREIGN KEY (`sell_record_id`) REFERENCES `ord_record` (`Id`);


/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
