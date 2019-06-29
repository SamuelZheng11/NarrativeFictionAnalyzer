class Entity:

    name = "";
    relations = [];
    inScenes = [];
    modifiers = [];

    def __init__(self, name):
        self.name = name;

    def addRelation(self, entity):
        self.relations.append(entity)

    def addSceneApperance(self, scene):
        self.inScenes.append(scene)

    def addModifier(self, modifier):
        self.modifiers.append(modifier)
