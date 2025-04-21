# Theodoridis Charalambos AM: 4674
# Bonitsis Pantelis AM: 4742
# Sidiropoulos Georgios AM: 4789

#! /usr/bin/env python3
import math
from rospkg import RosPack
import rospy
import queue
from re import T
from turtle import speed
from nav_msgs.msg import Odometry
from tf.transformations import euler_from_quaternion
from geometry_msgs.msg import Twist

x = 0.0
y = 0.0
theta = 0.0
ux = 0.0
uz = 0.0

# CallBack function is called when it is received an odometry message
def callBack(msg):
    global x
    global y
    global theta
    global ux
    global uz

    x = msg.pose.pose.position.x
    y = msg.pose.pose.position.y
    ux = msg.twist.twist.linear.x
    uz = msg.twist.twist.angular.z

    rot_q = msg.pose.pose.orientation
    (roll,pitch,theta) = euler_from_quaternion([rot_q.x,rot_q.y, rot_q.z,rot_q.w])

# Initialization of the node
rospy.init_node("speed_controller")

# Subsrciber for the topic odom
sub = rospy.Subscriber("/pioneer/odom",Odometry,callBack)
# Publisher for the topic cmd_vel
pub = rospy.Publisher("/pioneer/cmd_vel", Twist ,queue_size=1)

speed = Twist()
r = rospy.Rate(100)

# First move: The robot rotates until it reaches the direction of the interim position.
starting_time = rospy.Time.now().to_sec()
start_theta = theta
while rospy.Time.now().to_sec() - starting_time < 10.0:
    sec = rospy.Time.now().to_sec() - starting_time

    zt = 0.01390944*(sec**2) - 0.000927296*(sec**3)
    uzt = 0.02781888*(sec) - 0.002781888*(sec**2)
    speed.angular.z = uzt

    dis_z = abs(theta - start_theta)
    print("Angle of robot: ",theta," rad")
    print("Wanted distance that the robot has rotated: ",zt," rad")
    print("Real distance that the robot has rotated: ",dis_z," rad")
    print("Wanted angular velocity of robot: ",uzt," rad/sec")
    print("Real angular velocity of robot: ",uz," rad/sec")
    print(" ")


    pub.publish(speed)
    r.sleep

print("---------------------------------------------------------------------------------")

# Second move: The robot moves until it reaches the interim position.
starting_time = rospy.Time.now().to_sec()
while rospy.Time.now().to_sec() - starting_time < 210.0:
    sec = rospy.Time.now().to_sec() - starting_time

    xt = 0.001825361905*(sec**2) - 0.00000579479968*(sec**3)
    uxt = 0.00365072381*(sec) - 0.00001738439909*(sec**2)
    speed.linear.x = uxt

    dis_x = math.sqrt((x**2) + (y**2))
    print("(x,y) coordinates of robot: (",x,",",y,")")
    print("Wanted distance traveled by the robot: ",xt," m")
    print("Real distance traveled by the robot: ",dis_x," m")
    print("Wanted linear velocity of robot: ",uxt," m/sec")
    print("Real linear velocity of robot: ",ux," m/sec")
    print(" ")

    pub.publish(speed)
    r.sleep

print("---------------------------------------------------------------------------------")

starting_time = rospy.Time.now().to_sec()
while rospy.Time.now().to_sec() - starting_time < 10.0:
    speed.angular.z = 0.0
    speed.linear.x  = 0.0

    pub.publish(speed)
    r.sleep()

# Third move: The robot rotates until it reaches the direction of the final position.
starting_time = rospy.Time.now().to_sec()
start_theta = theta
while rospy.Time.now().to_sec() - starting_time < 10.0:
    sec = rospy.Time.now().to_sec() - starting_time

    zt = 0.06103332981*(sec**2) - 0.004068888654*(sec**3)
    uzt = 0.1220666596*(sec) - 0.01220666596*(sec**2)
    speed.angular.z= -uzt

    dis_z = abs(theta - start_theta)
    print("Angle of robot: ",theta," rad")
    print("Wanted distance that the robot has rotated: ",zt," rad")
    print("Real distance that the robot has rotated: ",dis_z," rad")
    print("Wanted angular velocity of robot: ",uzt," rad/sec")
    print("Real angular velocity of robot: ",uz," rad/sec")
    print(" ")


    pub.publish(speed)
    r.sleep

print("---------------------------------------------------------------------------------")

# Fourth move: The robot moves until it reaches the final position.
starting_time = rospy.Time.now().to_sec()
start_y = y
while rospy.Time.now().to_sec() - starting_time < 190.0:
    sec = rospy.Time.now().to_sec() - starting_time

    xt = 0.001994459834*(sec**2) - 0.00000699810468*(sec**3)
    uxt = 0.003988919668*sec-0.00002099431404*(sec**2)
    speed.linear.x = uxt

    dis_y = abs(y - start_y)
    print("(x,y) coordinates of robot: (",x,",",y,")")
    print("Wanted distance traveled by the robot: ",xt," m")
    print("Real distance traveled by the robot: ",dis_y," m")
    print("Wanted linear velocity of robot: ",uxt," m/sec")
    print("Real linear velocity of robot: ",ux," m/sec")
    print(" ")


    pub.publish(speed)
    r.sleep

print("---------------------------------------------------------------------------------")

# Fifth move: The robot rotates until it reaches the final angle.
starting_time = rospy.Time.now().to_sec()
start_theta = theta
while rospy.Time.now().to_sec() - starting_time < 10.0:
    sec = rospy.Time.now().to_sec() - starting_time

    zt = 0.1071238898*(sec**2) - 0.007141592654*(sec**3)
    uzt = 0.2142477796*(sec) - 0.02142477796*(sec**2)
    speed.angular.z = uzt

    dis_z = abs(theta - start_theta)
    print("Angle of robot: ",theta," rad")
    print("Wanted distance that the robot has rotated: ",zt," rad")
    print("Real distance that the robot has rotated: ",dis_z," rad")
    print("Wanted angular velocity of robot: ",uzt," rad/sec")
    print("Real angular velocity of robot: ",uz," rad/sec")
    print(" ")


    pub.publish(speed)
    r.sleep

print("Final (x,y) coordinates of robot: (",x,",",y,")")
print("Final angle of robot: ",theta," rad")
