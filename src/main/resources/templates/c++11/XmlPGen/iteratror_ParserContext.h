/* Part of XmlPGen
 * Copyright (c) 2013-2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
#ifndef XmlPGen_ParserContext_h_INCLUDED
#include "Log.h"
#include <iterator>

namespace XmlPGen
{
    class DOMParserNode;
    class DOMParserAttribute;
    class DOMParserNamespace;

    /**
     * Iterator across all attributes of a node.
     */
    class const_attribute_iterator : public ::std::iterator<::std::bidirectional_iterator_tag, DOMParserAttribute >
    {
    public:

        const_attribute_iterator(const_attribute_iterator const & aOther)
            : node_( aOther.node_)
            , currentAttributeIdx_( aOther.currentAttributeIdx_)
        {}

        const_attribute_iterator & operator = (const_attribute_iterator const & aOther)
        {
            node_ = aOther.node_;
            currentAttributeIdx_ = aOther.currentAttributeIdx_;
            return *this;
        }

        /**
         * Two iterators are equal if they refer to the same attribute.
         */
        inline bool operator==(const const_attribute_iterator& aOther)
        {
            return aOther.currentAttributeIdx_ == currentAttributeIdx_ && aOther.node_ == node_;
        }
        inline bool operator!=(const const_attribute_iterator& aOther)
        {
            return ! operator==(aOther);
        }

        /**
         * To the next attribute.
         */
        inline const_attribute_iterator& operator++()
        {
            ++currentAttributeIdx_;
            return *this;
        }

        /**
         * To the previous attribute.
         */
        inline const_attribute_iterator& operator--()
        {
            --currentAttributeIdx_;
            return *this;
        }

        /**
        * Postfix increment operator.
        */
        inline const_attribute_iterator operator++(int)
        {
            const_attribute_iterator tmpIt(*this);
            ++currentAttributeIdx_;
            return tmpIt;
        }

        /**
         * Postfix decrement operator.
         */
        inline const_attribute_iterator operator--(int)
        {
            const_attribute_iterator tmpIt(*this);
            --currentAttributeIdx_;
            return tmpIt;
        }

        /**
         * Access to the current attribute.
         */
        inline DOMParserAttribute const& operator*() const
        {
            return *(node_->getAttributeAt(currentAttributeIdx_));
        }

        inline DOMParserAttribute const* operator->() const
        {
            return node_->getAttributeAt(currentAttributeIdx_);
        }
    private:
        friend class attribute_container;
        const_attribute_iterator( DOMParserNode * node, uint32_t idx)
            : node_( node )
            , currentAttributeIdx_( idx )
        {}

        DOMParserNode * node_;
        uint32_t currentAttributeIdx_;
    };

    /**
     * Simple wrapper to enable a simple loop across all attributes.
     */
    class attribute_container
    {
    public:
        inline uint32_t size() const
        {
            return node_->getAttributeCount();
        }

        inline DOMParserAttribute const * getAttributeAt(uint32_t aIndex) const
        {
            return node_->getAttributeAt(aIndex);
        }

        const_attribute_iterator begin() const
        {
            return const_attribute_iterator(node_, 0);
        }
        const_attribute_iterator end() const
        {
            return const_attribute_iterator(node_, size());
        }

    private:
        friend class DOMParserNode;
        explicit attribute_container(DOMParserNode * node)
            : node_(node)
        {}

        DOMParserNode * node_;
    };


    /**
     * Iterator across all child-nodes of a node.
     */
    class const_node_iterator : public ::std::iterator<::std::bidirectional_iterator_tag, DOMParserNode >
    {
    public:
        /**
         * Two iterators are equal if they refer to the same child-node.
         */
        inline bool operator==(const const_node_iterator& aOther) const
        {
            return aOther.currentChildIdx_ == currentChildIdx_ && aOther.node_ == node_;
        }
        inline bool operator!=(const const_node_iterator& aOther) const
        {
            return ! operator==(aOther);
        }

        /**
         * To the next child-node.
         */
        inline const_node_iterator& operator++()
        {
            ++currentChildIdx_;
            return *this;
        }

        /**
         * To the previous child-node.
         */
        inline const_node_iterator& operator--()
        {
            --currentChildIdx_;
            return *this;
        }

        /**
         * Postfix increment operator.
         */
        inline const_node_iterator operator++(int)
        {
            const_node_iterator tmpIt(*this);
            ++currentChildIdx_;
            return tmpIt;
        }

        /**
         * Postfix decrement operator.
         */
        inline const_node_iterator operator--(int)
        {
            const_node_iterator tmpIt(*this);
            --currentChildIdx_;
            return tmpIt;
        }

        /**
        * Access to the current attribute.
        */
        inline DOMParserNode const& operator*() const
        {
            return *(node_->getChildAt(currentChildIdx_));
        }

        inline DOMParserNode const* operator->() const
        {
            return node_->getChildAt(currentChildIdx_);
        }
    private:
        friend class node_container;

        const_node_iterator(DOMParserNode const * node, int32_t idx)
            : node_(node_)
            , currentChildIdx_(idx)
        {}

        DOMParserNode const * node_;
        uint32_t currentChildIdx_;
    };


    /**
     * Simple wrapper to enable a simple loop across all child-nodes.
     */
    class node_container
    {
    public:
        inline uint32_t              size() const { return node_->getChildCount(); }
        inline DOMParserNode const * getNodeAt(uint32_t idx) const { return node_->getChildAt(idx); }
        
        const_node_iterator begin() const
        {
            return const_node_iterator(node_, 0);
        }
        const_node_iterator end() const
        {
            return const_node_iterator(node_, size());
        }

    private:
        friend class DOMParserNode;
        node_container(DOMParserNode *node)
            : node_(node)
        {}

        DOMParserNode const * node_;
    };

    /**
     * Accessor for a DOM node object.
     */
    class DOMParserNode
    {
    public:

        inline attribute_container const & attributes() const { return attributes_; }
        inline node_container      const & children() const { return nodes_; }

        inline int getToken() const { return token_; }
        virtual char const *          getTextContent()     const = 0;
        virtual uint32_t              getChildCount()      const = 0;
        virtual DOMParserNode const * getChildAt(uint32_t) const = 0;

        virtual uint32_t                   getAttributeCount()      const = 0;
        virtual DOMParserAttribute const * getAttributeAt(uint32_t) const = 0;

        virtual DOMParserNamespace const * getNamespace() const = 0;

    protected:
        explicit DOMParserNode(int token)
            : attributes_(this)
            , nodes_(this)
            , token_(token)
        {}

    private:
        attribute_container attributes_;
        node_container      nodes_;

        int const token_;
        
        DOMParserNode(const DOMParserNode&) = delete;
        DOMParserNode& operator = (const DOMParserNode&) = delete;
    };

    /**
     * Accessor for a DOM node object. 
     */
    class DOMParserNode
    {
    public:

        inline attribute_container const & attributes() const { return attributes_; }
        inline node_container      const & children() const { return nodes_; }

        inline int getToken() const { return token_; }
        virtual char const *          getTextContent()     const = 0;
        virtual uint32_t              getChildCount()      const = 0;
        virtual DOMParserNode const * getChildAt(uint32_t) const = 0;

        virtual uint32_t                   getAttributeCount()      const = 0;
        virtual DOMParserAttribute const * getAttributeAt(uint32_t) const = 0;

        virtual DOMParserNamespace const * getNamespace() const = 0;

    protected:
        explicit DOMParserNode(int token)
            : attributes_(this)
            , nodes_(this)
            , token_(token)
        {}

    private:
        attribute_container attributes_;
        node_container      nodes_;

        int const token_;

        DOMParserNode(const DOMParserNode&) = delete;
        DOMParserNode& operator = (const DOMParserNode&) = delete;
    };



    class DOMParserContext 
    {
    public:

        explicit DOMParserContext(::std::shared_ptr<Log>);
        DOMParserContext();

        DOMParserNode * getRoot();


        Log & getLogger() { return *logger_; }

    private:
        DOMParserContext(const DOMParserContext&) = delete;
        DOMParserContext& operator = (const DOMParserContext&) = delete;

        ::std::shared_ptr<Log> logger_;

    };
}

#endif