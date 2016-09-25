/**
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
package bweng.xmlpgen.xsd;

public interface ModelVisitor
{
	public void visitMember( int level, Member m );
	public void visitAttribute( int level, Attribute a );
	public void visitType( int level, Type t );
	public void visitNamespace( int level, Namespace ns );
	
}
