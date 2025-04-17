#CREATE DATABASE  IF NOT EXISTS `ApplicationManagmentApp`;

#Use `ApplicationManagmentApp`;

/*Drop table if exists `professor`;
Drop table if exists `student`;
Drop table if exists `subject`;
Drop table if exists `application`;
Drop table if exists `thesis`;*/


CREATE TABLE `users` (
  `id` int not null AUTO_INCREMENT,
  `user_name` text default null,
  `password` text default null,
  `role` text default null,
  Primary key (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 default CHARSET=latin1;


Create table  `professor`(
    `username` varchar(45) not null,
    `fullname` varchar(45) default null,
    `speciality` varchar(45) default null,
    Primary key(`username`)
);


Create table  `thesis`(
    `id` int not null ,
    `topic` varchar(45) default null,
    `professorusername` varchar(45) default null,
    `studentusername` varchar(45) default null,
    `studentfullname` varchar(45) default null,
    `implementationgrade` float default null,
    `reportgrade` float default null,
    `presentationgrade` float default null,
    `finalgrade` float,
    Primary key(`id`),
    CONSTRAINT `fk_professor_1` FOREIGN KEY (`professorusername`) REFERENCES `professor` (`username`)
);


create table `subject`(
    `id` int not null AUTO_INCREMENT,
    `professorusername` varchar(45) default null,
    `professorfullname` varchar(45) default null,
    `title` varchar(45) default null,
    `objectives` varchar(45) default null,
    Primary key (`id`),
    Constraint `fk_professor_2` foreign key (`professorusername`) references `professor`(`username`)
);


create table `student`(
    `username` varchar(45) not null,
    `fullname` varchar(45) default null,
    `yearofstudies` int default null,
    `currentaveragearade` float default null,
    `numberofremainingcourses` int default null,
    Primary key (`username`)
);


create table `application`(
    `id` int not null AUTO_INCREMENT,
    `studentusername` varchar(45) default null,
    `subjectid` int default null,
    Primary key (`id`),
    Constraint `fk_student` foreign key (`studentusername`) references `student`(`username`),
    Constraint `fk_subject` foreign key (`subjectid`) references `subject`(`id`)
);







