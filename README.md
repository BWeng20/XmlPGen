# XmlPGen
Basic goal of this parser generator is to generate a simple to handle data-model from some XML schema (xsd).
Support to parse and write XML from this model should be separated as far as possible.
Also any usage of a proprietary framework should be avoided.

This goal is currently archived by generate a set of c++11-classes for each data-type in the schema.
The only proprietary part is a generic "any"-type that is used to handle "any"-elements in the schema.

The generator can easily be configured (ini-file) to generate setters or only data-members, provide "is-set"-Flags, 
"protect" data-members or make them "public". 
Also the used base-types can be configured - but this may result in additional effort to adapt the generator code.

On top of these classes, a set of "type-handlers" and "writers" is generated.
The "type-handlers" bind the data-types to the generic parser - and can be re-used for other parser-types (e.g. json).
The "Writers" write the content via a generic XML-Writer.

The parser itself is generic, implemented via the libxml2-parser.

To build the generated code, the generated cmake-files can be used. 
If used in a real project-environment, additional templates can easily be added to generate the needed project-files.

It would make also sense, to move the generic parts to some external lib for easy re-use for different parsers.


