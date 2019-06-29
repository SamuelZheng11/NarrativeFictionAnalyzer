from narritive_objects.Entity import Entity


class Model:

    entities = []
    relationships = []
    locations = []
    scenes = []

    def addEntity(self, name):
        for entity in self.entities:
            if entity.name != name:
                self.entities.append(Entity(name))

    def getEntities(self):
        return self.entities
