from django.conf.urls.defaults import *
from django.views.generic.simple import direct_to_template

from api.models import Brewery

urlpatterns = patterns('',
    (r'map/', direct_to_template, {'template': 'map.html'}),
    (r'static/(?P<path>.*)$', 'django.views.static.serve', {
        'document_root': 'static',
        'show_indexes': True
    }),
    (r'bars/\((?P<lat>\d+(\.\d+)),(?P<lon>\d+(\.\d+))\)/', 'api.views.bars_near'),
    (r'bar/(?P<bar>\d+)', 'api.views.bar'),
    (r'beers/', 'api.views.all_beers'),
    (r'breweries/', 'api.views.all_breweries'),
    (r'changeBeer/', 'api.views.change_beer'),
    (r'addBar/', 'api.views.add_bar')
)
