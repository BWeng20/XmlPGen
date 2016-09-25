# XmlPGen
Basic goal of this parser generator is to generate a simple to handle data-model from some XML schema (xsd).
Support to parse and write XML from this model should be separated as far as possible.
Also any usage of a proprietary framework should be avoided.

This goal is currently archived by generate a set of c++11-classes for each data-type in the schema.
The only proprietary part is a generic "any"-type that is used to handle "any"-elements in the schema.

The generator can easily be configured (ini-file) to generate setters or only data-members, provide "is-set"-Flags, 
"protect" data-members or make them "public". 
Also the used basic types can be re-configured - but this may result in additional effort to adapt the generator code.

On top of these classes, a set of "type-handlers" and "writers" is generated.
The "type-handlers" bind the types to a generic parser - and can be re-used for other parser-types (e.g. json).
The "Writers" write the content via a generic XML-Writer.

The parsing itself is encapsulated in a generic part, currently implemented via the libxml2-parser.

To build the generated code, the also generated cmake-file can be used. 
If used in some project-environment, it may be needed to set-up some other build-system and to move the generic parts to some external lib.

