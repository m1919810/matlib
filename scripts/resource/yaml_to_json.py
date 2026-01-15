import yaml
import json

filename = "./matlib-test/src/main/resources/tests/codecs/item_stack.yaml"

with open(filename, "r", encoding="utf-8") as fp:
    obj = yaml.safe_load(fp)

print(json.dumps(obj, ensure_ascii= False, indent= 2))