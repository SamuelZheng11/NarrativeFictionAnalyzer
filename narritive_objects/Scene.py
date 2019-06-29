class Scene(object):
    pass

    locations = []
    characters = []

    def addCharacter(self, entity):
        self.characters.append(entity)

    def addLocation(self, location):
        self.locations.append(location)
