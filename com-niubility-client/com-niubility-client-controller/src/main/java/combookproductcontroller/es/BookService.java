package combookproductcontroller.es;

import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

public interface BookService {
    Optional<BookBean> findById(String id);

    BookBean save(BookBean blog);

    void delete(BookBean blog);

    Optional<BookBean> findOne(String id);

    List<BookBean> findAll();

    List<BookBean> findAll(int i, int b);

    Page<BookBean> findByAuthor(String author, PageRequest pageRequest);

    Page<BookBean> findByTitle(String title, PageRequest pageRequest);

    Iterable<BookBean> search(MatchPhraseQueryBuilder termQueryBuilder);
}
