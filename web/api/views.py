from models import Bar
import json

from django.http import HttpResponse
from api.models import Beer, Brewery
from bdb import bar

def bars_near(request, lat, lon):
    all_bars = find_closest(Bar, (39.963, -75.12), -1)
    ret_obj = {"bars": []}
    for b in all_bars:
        ret_obj["bars"].append({
            'id': b.id,
            'name': b.name,
            'location': [b.location.lon, b.location.lat],
            'taps': [{"id": beer.id, "name": beer.name, "brewery": beer.maker.name } for beer in b.taps.all()]
        })

    return HttpResponse(json.dumps(ret_obj))

# Find a max_count number of items of type, ordered by
# distance ascending from center.
def find_closest(type, center, max):
    all = type.objects.all()
    return all

def taps(request, bar):
    beers = Beer.objects.all(taps == bar)
    return (json.dumps(beers))

def all_beers(request):
    beers = Beer.objects.all()
    ret_obj = {"beers": [{"id": beer.id, "name": beer.name, "brewery": beer.maker.name } for beer in beers]}
    return HttpResponse(json.dumps(ret_obj))

def all_breweries(request):
    breweries = Brewery.objects.all()
    ret_obj = {"breweries": 
        [{
            "id": brewery.id, 
            "name": brewery.name,
            "beers": [{"name": beer.name, "id": beer.id} for beer in brewery.beer_set.all()]
        } for brewery in breweries]}
    return HttpResponse(json.dumps(ret_obj))
