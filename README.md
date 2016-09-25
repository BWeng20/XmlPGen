# XmlPGen
Baci goal of this parser generator is to generate a simple to handle data-model from some XML schema (xsd).
Support to parse and write XML from this model should be separated as far as possible.
Also any referecne to a properitary framework should be avoided.

This goal is currently archived by generate a set of c++11-classes for each data-type in the schema.
The only properitaty part in these classes is a generic "any"-type that is used to handle any "any"-elements in the schema.

The generator can easily be configured (ini-file) to generate setters or simple data-members, provide "set"-Flags, 
"protect" data-members or put them to "public". Also the basic types can be re-configured - but this may result in additional effort to addapt the generator code.


Separate a set of "type-handlers" and "Writers" is generated on top of these classes.
The "type-handlers" binds the types to a generic parser - and can be re-used for other parser-types (e.g. json).
The "Writers" for each type use a generic XML-Writer to write the content of the data-types.

