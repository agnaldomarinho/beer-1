import os

from django.conf.urls.defaults import *
from django.views.generic.simple import direct_to_template

from api.models import Brewery

lonlatre = '\((?P<lon>[+-]?\d+(\.\d+)),(?P<lat>[+-]?\d+(\.\d+))\)'

urlpatterns = patterns('',
    (r'^$', direct_to_template, {'template': 'home.html'}),
		(r'map/', direct_to_template, {'template': 'map.html'}),
    (r'static/(?P<path>.*)$', 'django.views.static.serve', {
        'document_root': os.path.join(os.path.dirname(__file__), 'static'),
        'show_indexes': True
    }),
    (r'bars/%s/'%lonlatre, 'api.views.bars_near'),
    (r'bar/(?P<bar>\d+)', 'api.views.bar'),
    (r'beers/', 'api.views.all_beers'),
    (r'breweries/', 'api.views.all_breweries'),
    (r'changeBeer/', 'api.views.change_beer'),
    (r'addBar/', 'api.views.add_bar'),
    (r'addBeer/(?P<bar_id>\d+)/(?P<position>\d+)', 'api.views.add_beer'),
    (r'removeTap/(?P<tap_id>\d+)', 'api.views.remove_tap'),
    (r'barTable/', 'api.views.bar_table'),
    (r'moveBar/(?P<bar_id>\d+)/%s/'%lonlatre, 'api.views.move_bar'),
    (r'osm/(?P<path>.*)', 'api.views.osm_cache')
)
