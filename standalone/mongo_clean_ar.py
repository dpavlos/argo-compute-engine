#!/usr/bin/env python

# arg parsing related imports
import os, sys
from subprocess import check_call
from argparse import ArgumentParser
from ConfigParser import SafeConfigParser
from pymongo import MongoClient



def main(args=None):

	# default config 
	fn_ar_cfg = "/etc/ar-compute-engine.conf"

	ArConfig = SafeConfigParser()
	ArConfig.read(fn_ar_cfg)
	
	mongo_host = ArConfig.get('default','mongo_host')
	mongo_port = ArConfig.get('default','mongo_port')

	mongo_service_dest = ArConfig.get('datastore_mapping','service_dest')
	mongo_egroup_dest = ArConfig.get('datastore_mapping','egroup_dest')

	#Split db.collection path strings to obtain database name and collection name
	mongo_service_dest = mongo_service_dest.split('.')
	db_service = mongo_service_dest[0]
	col_service = mongo_service_dest[1]

	mongo_egroup_dest = mongo_egroup_dest.split('.')
	db_egroup = mongo_egroup_dest[0]
	col_egroup = mongo_egroup_dest[1]

	#Create a date integer for use in the database queries
	date_int = int(args.date.replace("-",""))

	print "Connecting to mongo server: %s:%s" % (mongo_host,mongo_port)
	client = MongoClient(str(mongo_host), int(mongo_port))

	# for service collection cleanup do the following
	print "Regarding service a/r data..."

	print "Opening database: %s" % db_service
	db = client[db_service]

	print "Opening collection: %s" % col_service
	col = db[col_service]

	if args.profile:
		num_of_rows = col.find({"dt": date_int,"ap":args.profile}).count()
		print "Found %s entries for date %s and profile %s" % (num_of_rows,args.date,args.profile)	
	else:
		num_of_rows = col.find({"dt": date_int}).count()
		print "Found %s entries for date %s" % (num_of_rows,args.date)
		

	if num_of_rows > 0:	
		
		if args.profile:
			print "Remove entries for date: %s and av.profile: %s" % (args.date,args.profile) 
			col.remove({"dt": date_int,"ap":args.profile})
		else:
			print "Remove entries for date: %s" % args.date 
			col.remove({"dt": date_int})
			

		print "Entries Removed!"
	else:
		print "Zero entries found. No need to remove anything"


	# for service collection cleanup do the following
	print "Regarding endpoint group a/r data..."

	print "Opening database: %s" % db_egroup
	db = client[db_egroup]

	print "Opening collection: %s" % col_egroup
	col = db[col_egroup]

	if args.profile:
		num_of_rows = col.find({"dt": date_int,"ap":args.profile}).count()
		print "Found %s entries for date %s and profile %s" % (num_of_rows,args.date,args.profile)
	else:
		num_of_rows = col.find({"dt": date_int}).count()
		print "Found %s entries for date %s" % (num_of_rows,args.date)
		

	if num_of_rows > 0:	
		
		if args.profile:
			print "Remove entries for date: %s and av.profile: %s" % (args.date,args.profile) 
			col.remove({"dt": date_int,"ap":args.profile})
		else:
			print "Remove entries for date: %s" % args.date 
			col.remove({"dt": date_int})
			

		print "Entries Removed!"
	else:
		print "Zero entries found. No need to remove anything"


if __name__ == "__main__":

	# Feed Argument parser with the description of the 3 arguments we need (input_file,output_file,schema_file)
	arg_parser = ArgumentParser(description="clean status detail data for a day")
	arg_parser.add_argument("-d","--date",help="date", dest="date", metavar="DATE", required="TRUE")
	arg_parser.add_argument("-p","--profile",help="availability profile",dest="profile",metavar="STRING")

	# Parse the command line arguments accordingly and introduce them to main...
	sys.exit(main(arg_parser.parse_args()))