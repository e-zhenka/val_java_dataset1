public final int doWikiStartTag()
        throws IOException
    {
        WikiEngine engine = m_wikiContext.getEngine();
        WikiPage   page   = m_wikiContext.getPage();

        if( page != null )
        {
            if( page instanceof Attachment )
            {
                pageContext.getOut().print( ((Attachment)page).getFileName() );
            }
            else
            {
                pageContext.getOut().print( engine.beautifyTitle( m_wikiContext.getName() ) );
            }
        }

        return SKIP_BODY;
    }