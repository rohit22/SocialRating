import os
from flask import Flask, render_template, request, redirect, url_for, send_from_directory, json, jsonify, session
import omdb
import math

application = Flask(__name__)

import math
import pandas as pd
import re
import numpy as np
import ast
import json
import urllib
import urllib2

# print "Start"
# text = list(open("A Larger List.txt"))
# new_text=[]
# for line in text:
#     line = line.strip()
#     contents = line.split("\t")
#     new_text.append(contents)
# num_ham = 0
# num_spam = 0
# for line in new_text:
#     if line[0] == 'ham':
#         num_ham += 1
#     elif line[0] == 'spam':
#         num_spam += 1
# #7 powerful features
# wordcount = {}
# wordcount["/"] = 0 #/
# wordcount["+"] = 0 #+
# wordcount["len<=10"] = 0 #message length <= 10
# wordcount["digit>=10"] = 0 #>= 10 digits
# wordcount["digit>=5"] = 0 #>= 5 digits
# wordcount["digit>=3"] = 0 #>= 3 digits
# #Split train and test
# train_text = []
# for a in range(len(text)):
#     line = text[a]
#     line = line.strip()
#     contents = line.split("\t")
#     train_text.append(contents)
# train_num_ham = 0
# train_num_spam = 0
# for line in train_text:
#     if line[0] == 'ham':
#         train_num_ham += 1
#     elif line[0] == 'spam':
#         train_num_spam += 1
# stopwordlist = urllib2.urlopen('http://www.textfixer.com/resources/common-english-words.txt')
# for line in stopwordlist:
#     stopword = line.split(",")
# new_word1 = []
# for line in train_text:
#     new_words = []
#     if sum(1 for c in line[1] if c == "/") >= 1:
#         temp = sum(1 for c in line[1] if c == "/")
#         wordcount["/"] += temp
#         new_words.append(["/",temp])
#     if sum(1 for c in line[1] if c == "+") >= 1: 
#         temp = sum(1 for c in line[1] if c == "+")
#         wordcount["+"] += temp
#         new_words.append(["+",temp])
#     k = line[1].lower()
#     k = k.replace("'","")
#     k = re.sub(r'[^\w]', ' ', k)
#     k = k.split()
#     if len(k) <= 10:
#         wordcount["len<=10"] += 1
#         new_words.append(["len<=10",1])
#     for word in k:
#         if sum(1 for c in word if c.isdigit()) >= 10:
#             wordcount["digit>=10"] += 1
#             new_words.append(["digit>=10",1])
#         elif sum(1 for c in word if c.isdigit()) >= 5:
#             wordcount["digit>=5"] += 1
#             new_words.append(["digit>=5",1])
#         elif sum(1 for c in word if c.isdigit()) >= 3:
#             wordcount["digit>=3"] += 1
#             new_words.append(["digit>=3",1])
#         if word not in stopword:
#             new_words.append([word,1])
#             if word not in wordcount:
#                 wordcount[word] = 1
#             else:
#                 wordcount[word] += 1
#     new_word1.append([line[0],new_words])
# new_word2 = []
# for line in new_word1:
#     sub_dict = {}
#     for word in line[1]:
#         if word[0] not in sub_dict:
#             sub_dict[word[0]] = word[1]
#         else:
#             sub_dict[word[0]] += word[1]
#     new_word2.append([line[0],sub_dict])
# #greater than 5
# wordcount1 = {}
# for word in wordcount:
#     if wordcount[word] >= 5:
#         wordcount1[word] = wordcount[word]
        
# new_word3 = []
# for line in new_word2:
#     asd = {}
#     for word in line[1]:
#         if word in wordcount1:
#             asd[word] = line[1][word]
#     new_word3.append([line[0], asd])
    
# df1 = pd.DataFrame.from_dict(wordcount1, orient = "index")
# df2 = df1.transpose()
# for line in new_word3:
#     df2 = df2.append(line[1],ignore_index = True)
# df4 = df2.replace('NaN',0)
# type1=["Total"]
# for line in new_word2:
#     type1.append(line[0])
# from sklearn import tree
# clf = tree.DecisionTreeClassifier()
# clf = clf.fit(df4, type1)
# df10 = df1.transpose()
# wordcount2 = {}
# for word in wordcount1:
#     wordcount2[word] = 0

# print "Finished building model"

import pickle
from sklearn.externals import joblib
clf = joblib.load('model/tree.pkl') 
df110 = pd.read_pickle("model/df1.pkl")
wordcount2 = joblib.load('model/wordcount2.pkl')
global twitter_sample
global youtube_sample
#global df110
#global df110
df10 = df110.transpose()
#global df110
#line = "I am win lottery call now prize 1231232134235"
#line = "I am happy"
def spamclass(lines,clf,df10,wordcount2):
    for line in lines:
        new_words = []
        if sum(1 for c in line if c == "/") >= 1:
            temp = sum(1 for c in line if c == "/")
            new_words.append(["/",temp])
        if sum(1 for c in line if c == "+") >= 1: 
            temp = sum(1 for c in line if c == "+")
            new_words.append(["+",temp])
        k = line.lower()
        k = k.replace("'","")
        k = re.sub(r'[^\w]', ' ', k)
        k = k.split()
        if len(k) <= 10:
            new_words.append(["len<=10",1])
        for word in k:
            if sum(1 for c in word if c.isdigit()) >= 10:
                new_words.append(["digit>=10",1])
            elif sum(1 for c in word if c.isdigit()) >= 5:
                new_words.append(["digit>=5",1])
            elif sum(1 for c in word if c.isdigit()) >= 3:
                new_words.append(["digit>=3",1])
            if word in wordcount2:
                new_words.append([word,1])
        sub_dict = {}
        for word in new_words:
            if word[0] not in sub_dict:
                sub_dict[word[0]] = word[1]
            else:
                sub_dict[word[0]] += word[1]
        df10 = df10.append(sub_dict,ignore_index = True)
        df11 = df10.replace('NaN',0)
    result = clf.predict(df11)
    outcome_list = []
    for i in range(len(result)-1):
        if result[i+1] == 'ham':
            outcome_list.append(lines[i])
    return outcome_list

#url = "http://socialratingsearch-env.us-east-1.elasticbeanstalk.com/api/getContent"

@application.route('/')
@application.route('/index')
def index():
    return render_template('index.html')

# youtube_sample = []
# twitter_sample = []

@application.route('/search', methods=['POST'])
def search():
	print 'hello'
	term = request.form['keyword']
	session['keyword'] = term
	try:
		param = {"query": {"word":term,"tweets" : "300","ytcomments" : "300",}}
		req = urllib2.Request("http://socialratingsearch-env.us-east-1.elasticbeanstalk.com/api/getContent")
		req.add_header('Content-Type', 'application/json')
		response = urllib2.urlopen(req, json.dumps(param)).read()
	except: # take care of all those ugly errors if there are some
		print("error")
	data = json.loads(response)
	twitter = []
	youtube = []
	for line in data['results']:
		if line['type'] == "twitter":
			if line['topicProb'] >= 0.35:
				twitter.append(line['text'])
		elif line["type"] == "youtube":
			if line['topicProb'] >= 0.35:
				youtube.append(line['text'])
 	youtube_comments = youtube
	twitter_comments = twitter
	print len(twitter_comments)
	print len(youtube_comments)
	twitter_score = 0
	youtube_score = 0
	global twitter_sample
	twitter_sample = []
	if len(twitter_comments) > 0:
		k = spamclass(twitter_comments[:500],clf,df10,wordcount2)
		twitter_pos = float(0)
		twitter_neutral = float(0)
		twitter_neg = float(0)
		count = 0
		i = 0
		for sen in k:
			data = urllib.urlencode({"text": sen.encode('utf-8')})
			u = urllib.urlopen("http://text-processing.com/api/sentiment/", data)
			the_page = u.read()
			try:
				twitter_prob = ast.literal_eval(the_page)['probability']
				twitter_pos += twitter_prob['pos']
				#neutral += prob['neutral']
				twitter_neg += twitter_prob['neg']
				if i < 9:
					twitter_sample.append([sen,math.sqrt(twitter_prob['pos'])*10])
					i += 1
			except:
				count += 1
				pass
		#twitter_sample = twitter_temp_sample
		print "twitter_pos: " + str(twitter_pos/(len(k)-count))
		#print "neutral: " + str(neutral/len(k))
		print "twitter_neg: " + str(twitter_neg/(len(k)-count))
		twitter_score = math.sqrt(twitter_pos/(len(k)-count))
		print term
		print "twitter_score: " + str(twitter_score*10)
		# df10 = df110.transpose()
	else:
		print "No tweets"
	#df10 = df110.transpose()
	global youtube_sample
	youtube_sample = []
	if len(youtube_comments) > 0:
		k = spamclass(youtube_comments[:500],clf,df10,wordcount2)
		youtube_pos = float(0)
		youtube_neutral = float(0)
		youtube_neg = float(0)
		count = 0
		i = 0
		for sen in k:
			data = urllib.urlencode({"text": sen.encode('utf-8')})
			u = urllib.urlopen("http://text-processing.com/api/sentiment/", data)
			the_page = u.read()
			try:
				youtube_prob = ast.literal_eval(the_page)['probability']
				youtube_pos += youtube_prob['pos']
				youtube_neg += youtube_prob['neg']
				if i < 9:
					youtube_sample.append([sen,math.sqrt(youtube_prob['pos'])*10])
					i += 1
			except:
				count += 1
				pass
			#neutral += prob['neutral']
		#youtube_sample = youtube_temp_sample
		print "youtube_pos: " + str(youtube_pos/(len(k)-count))
		#print "neutral: " + str(neutral/len(k))
		print "youtube_neg: " + str(youtube_neg/(len(k)-count))
		youtube_score = math.sqrt(youtube_pos/(len(k)-count))
		print term
		print "youtube_score: " + str(youtube_score*10)
	else:
		print "No youtubes"	# print 'hello'
	# term = request.form['keyword']
	# print term
	if len(twitter_sample) > 0:
		print twitter_sample
	if len(youtube_sample) > 0:
		print youtube_sample
	res = omdb.get(title=term, tomatoes=True)
	imdb_rating = res.imdb_rating
	print "imdb score: " + str(imdb_rating)
	term = res.title
	plot = res.plot
	tomato_rating = res.tomato_rating
	
	float_formatter = lambda x: "%.1f" % x
	youtube_rating = youtube_score*10
	twitter_rating = twitter_score*10
	if youtube_rating == 0:
		overall_rating = float_formatter(twitter_rating)
		youtube_img = "Images/NA.png"
		twitter_img = "Images/" + str(math.trunc(twitter_rating)) + ".png"
	elif twitter_rating == 0:
		overall_rating = float_formatter(youtube_rating)
		twitter_img = "Images/NA.png"
		youtube_img = "Images/" + str(math.trunc(youtube_rating)) + ".png"
	else:
		overall_rating = (youtube_rating + twitter_rating)/2
		twitter_img = "Images/" + str(math.trunc(twitter_rating)) + ".png"
		youtube_img = "Images/" + str(math.trunc(youtube_rating)) + ".png"

	youtube_rating = float_formatter(youtube_rating)
	twitter_rating = float_formatter(twitter_rating)
	if (imdb_rating=="N/A"):
		imdb_rating = imdb_rating.replace("/", "")
	if (tomato_rating=="N/A"):
		tomato_rating = tomato_rating.replace("/", "")
	
	overall_rating = str(overall_rating)
	overall_img = "Images/" + overall_rating.split('.', 1)[0] + ".png"
	imdb_img = "Images/" + imdb_rating.split('.', 1)[0] + ".png"
	tomato_img = "Images/" + tomato_rating.split('.', 1)[0] + ".png"
	return render_template('ratings.html', keyword=term, plot=plot, imdb_rating=imdb_rating, youtube_rating=youtube_rating, \
		tomato_rating=tomato_rating, twitter_rating=twitter_rating, overall_rating=overall_rating, overall_img=overall_img, \
		youtube_img=youtube_img, twitter_img=twitter_img, imdb_img=imdb_img, tomato_img=tomato_img)

@application.route('/twitter')
def twitter():
	term = session.get('keyword', None)
	comment_list = []
	float_formatter = lambda x: "%.1f" % x
	global twitter_sample
	for item in twitter_sample:
		img = "Images/" + str(math.trunc(item[1])) + ".png"
		comment_list.append({"text": item[0], "rating": float_formatter(float(item[1])), "img":img})

	return render_template('twitter.html', keyword=term, twitter_comment=comment_list)

@application.route('/youtube')
def youtube():
	term = session.get('keyword', None)
	comment_list = []
	float_formatter = lambda x: "%.1f" % x
	global youtube_sample
	for item in youtube_sample:
		img = "Images/" + str(math.trunc(item[1])) + ".png"
		comment_list.append({"text": item[0], "rating": float_formatter(float(item[1])), "img":img})
	return render_template('youtube.html', keyword=term, youtube_comment=comment_list)


if __name__ == '__main__':
    application.secret_key = ']\x0cWf\xac\xb9\xbe\x8e1\xefWN4|\xac\x1c\xf1\xa3\x89BXn\xd8c'
    application.run(host='0.0.0.0',debug=True)