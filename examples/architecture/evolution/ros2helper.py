from std_msgs.msg import Float64

def publishFloat(publisher, value):
	msg = Float64()
	msg.data = value
	publisher.publish(msg)
