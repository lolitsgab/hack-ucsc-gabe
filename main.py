from google.appengine.api import users
from google.appengine.api import images


from google.appengine.ext import ndb
from google.appengine.ext import blobstore

import jinja2
import logging
import webapp2
import os
import cgi
import urllib



template_dir = os.path.join(os.path.dirname(__file__), 'templates')
jinja_environment = jinja2.Environment(
  loader=jinja2.FileSystemLoader(template_dir))

class User(ndb.Model):
    username = ndb.StringProperty()
    email = ndb.StringProperty()

class Clothes(ndb.Model):
    clothes_name = ndb.StringProperty()
    clothes_type = ndb.StringProperty()

    content = ndb.TextProperty()
    picture = ndb.BlobProperty()

    color = ndb.StringProperty() # could be changed to triple to hold RGB value
    user_key = ndb.KeyProperty(kind=User)



class Photo(ndb.Model):
    title = ndb.StringProperty()
    full_size_image = ndb.BlobProperty()


class Thumbnailer(webapp2.RequestHandler):
    def get(self):
        if self.request.get("id"):
            photo = Photo.get_by_id(int(self.request.get("id")))

            if photo:
                img = images.Image(photo.full_size_image)
                # img.resize(width=200, height=200)
                # img.im_feeling_lucky()
                thumbnail = img.execute_transforms(output_encoding=images.png)

                self.response.headers['Content-Type'] = 'image/png'
                self.response.out.write(thumbnail)
                return

        # Either "id" wasn't provided, or there was no image with that ID
        # in the datastore.
        self.error(404)

class Image(webapp2.RequestHandler):
    def get(self):
        image_key = ndb.Key(urlsafe=self.request.get('img_id'))
        image = image_key.get()
        if Clothes.picture:
            self.response.headers['Content-Type'] = 'image/png'
            self.response.out.write(image.picture)
        else:
            self.response.out.write('No image')

class MainHandler(webapp2.RequestHandler):
    def get(self):

        current_user = users.get_current_user()

        if current_user:
            logout_url = users.CreateLogoutURL('/')

            # create username based on email address
            email = current_user.email()
            username = email.rsplit('@')[0]
            # interact with db
            current_users = User.query(User.email==email).fetch()

            if not current_users: # if the user doesn't exist in the current users list
                user = User(username=username, email=email)
                user.put()
            else:
                user = User.query(User.username == username).get()
                clothes = Clothes.query(Clothes.user_key==user.key)
                for clothing in clothes:
                    self.response.out.write('<div><img src="/img?img_id=%s"></img>' %
                        Clothes.key.urlsafe())
                    self.response.out.write('<br><blockquote>%s</blockquote></div>' %
                        cgi.escape(Clothes.clothes_name))

                clothes_name = self.request.get('clothes_name')



            template = jinja_environment.get_template('/process.html')
            template_vals = {'user':user, 'logout_url':logout_url}
            #
            self.response.write(template.render(template_vals))

        else:
            login_url = users.CreateLoginURL('/')

            template = jinja_environment.get_template('/home.html')

            template_vals = {'login_url':login_url}
            self.response.write(template.render(template_vals))

    def post(self):
        # get info
        current_user = users.get_current_user()
        email = current_user.email()

        user = User.query(User.email == email).get()

        note = self.request.get('note')
        # interact with db

        # render
        picture = self.request.get('img')
        new_clothing.picture = picture
        new_clothing.put()
        self.redirect('/process.html')


        # self.redirect('/')

class ClothesHandler(webapp2.RequestHandler):

    def get(self):
        # get info
        current_user = users.get_current_user()
        email = current_user.email()

        user = User.query(User.email == email).get()

        user_key = user.key

        if self.request.get("id"):
            photo = Photo.get_by_id(int(self.request.get("id")))

            if photo:
                img = images.Image(photo.full_size_image)
                # img.resize(width=200, height=200)
                # img.im_feeling_lucky()
                thumbnail = img.execute_transforms(output_encoding=images.png)

                self.response.headers['Content-Type'] = 'image/png'
                self.response.out.write(thumbnail)
                return

        # # need respective html file
        # template = jinja_environment.get_template('upload.html')
        #
        #
        #
        #
        #
        # logout_url = users.CreateLogoutURL('/')
        # self.response.write(template.render({'user':user, 'logout_url':logout_url}))

    def post(self):
        current_user = users.get_current_user()
        email = current_user.email()

        user = User.query(User.email == email).get()
        # image code
        user_key = user.key



        # image code

        # clothes_name = self.request.get('clothes_name')

        new_clothing = Clothes(clothes_name=clothes_name, user_key=user_key)

        picture = self.request.get('img')
        new_clothing.picture = picture
        new_clothing.put()
        self.redirect('/?' + urllib.urlencode(
            {'clothes_name': clothes_name})
            )

class Process(webapp2.RequestHandler):

    def get(self):
        current_users = User.query(User.email==email).fetch()
        user = User.query(User.username == username).get()
        clothes = Clothes.query(Clothes.user_key==user.key)
        for clothing in clothes:

            clothes_name = self.request.get('clothes_name')

app = webapp2.WSGIApplication([
    ('/', MainHandler), ('/clothes', ClothesHandler),
], debug=True)
