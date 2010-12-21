from django.db import models

class Location(models.Model):
    lon = models.FloatField()
    lat = models.FloatField()

class Brewery(models.Model):
    name = models.CharField(max_length=200)
    location = models.OneToOneField(Location)

class Beer(models.Model):
    maker = models.ForeignKey(Brewery)
    name = models.CharField(max_length=200)

class Bar(models.Model):
    name = models.CharField(max_length=200)
    taps = models.ManyToManyField(Beer, through="Tap")
    location = models.ForeignKey(Location)

class Tap(models.Model):
    bar = models.ForeignKey(Bar)
    beer = models.ForeignKey(Beer)
    position = models.IntegerField()
