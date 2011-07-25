from django.shortcuts import render_to_response
from models import Bar
import json
import os
import datetime

import httplib2
from django.http import HttpResponse
from api.models import Beer, Brewery, Tap, Location

def bars_near(request, lat, lon):
    all_bars = find_closest(Bar, (39.963, -75.12), -1)
    ret_obj = {"bars": []}
    for b in all_bars:
        ret_obj["bars"].append({
            'id': b.id,
            'name': b.name,
            'location': [b.location.lon, b.location.lat],
            "taps": [
            {
                "id": tap.id, 
                "position": tap.position, 
                "beer": {
                    "id": tap.beer.id, 
                    "brewery": {"name": tap.beer.maker.name, "id": tap.beer.maker.id}, 
                    "name": tap.beer.name
                }
            } for tap in Tap.objects.order_by('position').filter(bar = b)]
        })

    return HttpResponse(json.dumps(ret_obj))

# Find a max_count number of items of type, ordered by
# distance ascending from center.
def find_closest(type, center, max):
    all = type.objects.all()
    return all

def bar(request, bar):
    bar = Bar.objects.get(id = bar)
    ret_obj = {
        "bar": {
            "id": bar.id, 
            "taps": [
            {
                "id": tap.id, 
                "position": tap.position, 
                "beer": {
                    "id": tap.beer.id, 
                    "brewery": {"name": tap.beer.maker.name, "id": tap.beer.maker.id}, 
                    "name": tap.beer.name
                }
            } for tap in Tap.objects.order_by('position').filter(bar = bar)]
        }
    }
    return HttpResponse(json.dumps(ret_obj))

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

def change_beer(request):
    json_string = request.REQUEST["json"]
    data = json.loads(json_string)
    tap_id = data["tap"]

    # try to grab the brewery id.  if there is none, create the brewery
    if "breweryId" in data:
        brewery = Brewery.objects.get(id = data["breweryId"])
    else:
        location = Location(lon = 0, lat = 0)
        location.save()
        brewery = Brewery(name = data["breweryName"], location = location)
        brewery.save()

    if "beerId" in data:
        beer = Beer.objects.get(id = data["beerId"])
    else:
        beer = Beer(name = data["beerName"], maker = brewery)
        beer.save()

    tap = Tap.objects.get(id = tap_id)
    tap.beer = beer
    tap.save()

    return HttpResponse("success")

def add_bar(request):
    json_string = request.REQUEST["json"]
    data = json.loads(json_string)
    bar_name = data["barName"]
    location = Location(lon = 0, lat = 0)
    location.save()
    b = Bar(name = bar_name, location = location)
    b.save()
    return HttpResponse(json.dumps({"bar": {"name": b.name, "id": b.id}}))

def add_beer(request, bar_id, position):
    bar_id = int(bar_id)
    position = int(position)
    print 'position is %d' % position
    bar = Bar.objects.get(id = bar_id)
    taps = Tap.objects.order_by('position').filter(bar = bar)
    curr_position = 1
    # iterate over all the taps in position order, and reassign their position
    # when we see the position we want to add, leave a space for it
    for tap in taps:
        if curr_position == position:
            curr_position += 1
        tap.position = curr_position
        tap.save()
        curr_position += 1

    unknown = Beer.objects.get(id = 1)
    tap = Tap(beer = unknown, bar = bar, position = position)
    tap.save()

    return HttpResponse(json.dumps({"status": "success"}))

def remove_tap(request, tap_id):
    tap_id = int(tap_id)
    tap = Tap.objects.get(id = tap_id)
    tap.delete()
    return HttpResponse(json.dumps({"status": "success"}))

def bar_table(request):
    bars = Bar.objects.all()
    ret = []
    for bar in bars:
        ret.append([
            bar.name,
            bar.location.lon,
            bar.location.lat
        ])
    return HttpResponse(json.dumps({"aaData": ret}))

def move_bar(request, bar_id, lon, lat):
    bar_id = int(bar_id)
    bar = Bar.objects.get(id = bar_id)
    bar.location.lon = lon
    bar.location.lat = lat
    bar.location.save()
    return HttpResponse(json.dumps({"status": "success"}))

def log(logme):
    print "[%s] %s" % (datetime.datetime.now(), logme)

def osm_cache(request, path):
    log('start')
    base_path = "/tmp/osm_cache/"
    # try to read the file.  if it exists, load it and return it
    try:
        f = open(base_path + path, 'rb')
        log('reading from file')
        return HttpResponse(content=f.read(), mimetype="image/png")
    except IOError:
        conn = httplib2.Http()
        url = "http://tile.openstreetmap.org/" + path
        log('reading from web')
        resp, content = conn.request(url, request.method)
        new_path = base_path + '/'.join(path.split('/')[:-1])
        if not os.path.exists(new_path):
            os.makedirs(new_path)
        f = open(base_path + path, 'wb')
        f.write(content)
        f.close()
        return HttpResponse(content=content, mimetype="image/png")


def bar_detail(request, bar_id):
    bar_id = int(bar_id)
    bar = Bar.objects.get(id = bar_id)
    return render_to_response('bar_detail.html', { 'bar': bar })